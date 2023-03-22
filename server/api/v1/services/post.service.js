const Post = require("../models/post.model");
const sequelize = require("../config");

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
      },
    ],
  });
  return await getPostById(newPost.id);
};

const updatePost = async (id, post) => {
  const result = await Post.update(
    post,
    common({
      where: { id },
      returning: true,
      individualHooks: true,
    })
  );
  return [result[0], [await getPostById(id)]];
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
module.exports = {
  getAll,
  getPostById,
  createPost,
  updatePost,
  deletePost,
  increaseLikePost,
  decreaseLikePost,
  model: Post,
};
