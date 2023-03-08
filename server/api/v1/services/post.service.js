const Post = require("../models/post.model");

const OFFSET = 0;
const LIMIT = 10;

const getAll = ({ page, limit, order }) => {
  var offset = OFFSET;
  if (page) offset = (page - 1) * limit;
  if (!limit) limit = LIMIT;

  return Post.findAll({
    offset,
    limit,
    order: [["createdAt", order ? order : "DESC"]],
    include: [
      {
        association: "User",
        attributes: ["id", "email"],
      },
    ],
  });
};

const getPostById = (id) => {
  return Post.findOne({
    where: { id },
  });
};

const createPost = (post) => {
  return Post.create(post, {
    include: [
      {
        association: "User",
      },
    ],
  });
};

const updatePost = (id, post) => {
  return Post.update(post, { where: { id } });
};

const deletePost = (id) => {
  return Post.destroy({ where: { id } });
};

module.exports = {
  getAll,
  getPostById,
  createPost,
  updatePost,
  deletePost,
};
