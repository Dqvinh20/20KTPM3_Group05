const PostService = require("../services/post.service");

const getAll = async (req, res) => {
  try {
    const posts = await PostService.getAll(req.query);
    res.json(posts);
  } catch (error) {
    res.json(error);
  }
};

const createPost = async (req, res) => {
  const { title, brief_description, created_by } = req.body;
  try {
    const post = await PostService.createPost({
      title,
      brief_description,
      created_by,
    });
    return res.json(post);
  } catch (error) {
    return res.json(error);
  }
};

const updatePost = async (req, res) => {
  const { post_id, ...data } = req.body;

  try {
    const post = await PostService.updatePost(post_id, data);
    return res.json(post);
  } catch (error) {
    return res.json(error);
  }
};

const deletePost = async (req, res) => {
  const { post_id } = req.body;

  try {
    const post = await PostService.deletePost(post_id);
    return res.json(post);
  } catch (error) {
    return res.json(error);
  }
};

module.exports = {
  getAll,
  createPost,
  updatePost,
  deletePost,
};
