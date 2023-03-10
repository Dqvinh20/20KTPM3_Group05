const Post = require("../models/post.model");

const OFFSET = 0;
const LIMIT = 10;

const common = (query) => {
  return {
    ...query,
    attributes: {
      exclude: ["created_by"],
    },
    include: [
      {
        association: "author",
        attributes: ["id", "email", "avatar"],
      },
    ],
  };
};

const getAll = async ({ page, limit, order }) => {
  var offset = OFFSET;
  if (page) offset = (page - 1) * limit;
  if (!limit) limit = LIMIT;

  return await Post.findAll(
    common({
      offset,
      limit,
      order: [["createdAt", order ? order : "DESC"]],
    })
  );
};

const getPostById = async (id) => {
  return await Post.findByPk(id, common({}));
};

const createPost = async (post_data) => {
  const newPost = await Post.create(post_data, {
    include: [
      {
        association: "author",
      },
    ],
  });
  return await getPostById(newPost.id);
};

const updatePost = async (id, post) => {
  return await Post.update(post, { where: { id } });
};

const deletePost = async (id) => {
  return await Post.destroy({ where: { id } });
};

module.exports = {
  getAll,
  getPostById,
  createPost,
  updatePost,
  deletePost,
};
