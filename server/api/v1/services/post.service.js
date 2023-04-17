const Post = require("../models/post.model");
const sequelize = require("../config");
const cloudinary = require("../utils/cloudinary");

const queryAuthor = {
  association: "author",
  attributes: ["id", "email", "avatar", "user_name", "user_name_non_accent"],
};

const common = (query) => {
  return {
    attributes: {
      exclude: ["created_by"],
    },
    include: [{ ...queryAuthor }],
    ...query,
  };
};

const getAll = async ({ offset, limit, order, no_limit, ...options }) => {
  if (no_limit) {
    limit = null;
    offset = null;
  }

  const posts = await Post.findAll(
    common({
      offset,
      limit,
      ...options,
      order: [["createdAt", order ? order : "DESC"]],
    })
  );

  return posts;
};

const getPostById = async (id) => {
  const post = await Post.findByPk(
    id,
    common({
      include: [
        {
          ...queryAuthor,
        },
        {
          association: "schedules",
          separate: true,
          order: [["date", "ASC"]],
        },
      ],
    })
  );
  await Promise.all(
    post.schedules.map(async (schedule) => {
      const locations = await schedule.getLocations({
        attributes: {
          exclude: ["place_id", "_search"],
        },
      });
      locations.sort((a, b) => {
        a.SchedulesLocations.position - b.SchedulesLocations.position;
      });
      await schedule.setDataValue("locations", locations);
    })
  );

  return post;
};

const createPost = async (post_data) => {
  if (!post_data.cover_img) {
    delete post_data.cover_img;
  }
  if (!post_data.brief_description) {
    delete post_data.brief_description;
  }
  const newPost = await Post.create(post_data, {
    include: [
      {
        association: "author",
      },
      {
        association: "schedules",
        include: [{ association: "locations", exclude: ["place_id"] }],
      },
    ],
  });
  return await getPostById(newPost.id);
};

const updatePost = async (id, post) => {
  const oldCoverImg = await Post.findByPk(id, { attributes: ["cover_img"] });

  if (
    oldCoverImg.cover_img &&
    oldCoverImg.cover_img !==
      "https://res.cloudinary.com/dkzlalahi/image/upload/q_90/v1681707145/default_trip_plan_cover_img.png"
  ) {
    cloudinary.destroy(oldCoverImg.cover_img);
    console.log("delete cover img");
  }
  console.log("oldCoverImg", oldCoverImg.cover_img);
  const result = await Post.update(
    post,
    common({
      where: { id },
      returning: true,
      individualHooks: true,
    })
  );

  return [result[0], await getPostById(id)];
};

const deletePost = async (id) => {
  return await Post.destroy({ where: { id } });
};

const increaseLikePost = async (id) => {
  return await Post.update(
    { like_count: sequelize.literal("like_count + 1") },
    { where: { id } }
  );
};
const decreaseLikePost = async (id) => {
  return await Post.update(
    { like_count: sequelize.literal("like_count - 1") },
    { where: { id } }
  );
};

const getPostByIdLocation = async (location_id) => {
  try {
    const post = await Post.findAll(
      common({
        order: [["createdAt", "DESC"]],
        include: [
          { ...queryAuthor },
          {
            association: "schedules",
            attributes: [],
            right: true,
            include: [
              {
                association: "locations",
                attributes: [],
                where: { id: location_id },
              },
            ],
          },
        ],
        where: { is_public: true },
        limit: 10,
      })
    );
    return post;
  } catch (err) {
    console.log(err);
  }
};
module.exports = {
  getAll,
  getPostById,
  createPost,
  updatePost,
  deletePost,
  increaseLikePost,
  decreaseLikePost,
  getPostByIdLocation,
  model: Post,
};
