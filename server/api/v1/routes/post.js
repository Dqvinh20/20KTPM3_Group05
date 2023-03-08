const router = require("express").Router();
const PostController = require("../controllers/post.controller");

router.get("/", PostController.getAll);

router.post("/create", PostController.createPost);

router.patch("/update", PostController.updatePost);

router.delete("/delete", PostController.deletePost);

module.exports = router;
