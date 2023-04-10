const Post = require("../models/post.model");
const sequelize = require("../config");

const queryAuthor = {
  association: "author",
  attributes: [
    "id",
    "email",
    "avatar",
    "user_name",
    "user_name_non_accent",
    "name",
  ],
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
  return post;
};

const createPost = async (post_data) => {
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
  // console.log(oldCoverImg.cover_img);
  const result = await Post.update(
    post,
    common({
      where: { id },
      returning: true,
      individualHooks: true,
    })
  );

  // if (post.cover_img) {
  //   cloudinary.destroy(oldCoverImg.cover_img);
  // }

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
