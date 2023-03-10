const PostService = require("../services/post.service");
const cloudinary = require("../utils/cloudinary");

const getAll = async (req, res) => {
  try {
    const posts = await PostService.getAll(req.query);
    res.json(posts);
  } catch (error) {
    res.json(error);
  }
};

const createPost = async (req, res) => {
  var cover_img = null;
  if (req.file) {
    const upload_img = await Promise.resolve(
      cloudinary.uploadStream(req.file.buffer, {
        folder: "posts/" + req.user.id,
      })
    );
    cover_img = upload_img.url;
  }

  const { title, brief_description, start_date, end_date } = req.body;
  const created_by = req.user.id;

  try {
    const post = await PostService.createPost({
      title,
      brief_description,
      cover_img,
      start_date,
      end_date,
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
  const post = await PostService.getPostById(post_id);
  cloudinary.destroy(post.cover_img);
  try {
    const result = await PostService.deletePost(post_id);
    return res.json(result);
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
