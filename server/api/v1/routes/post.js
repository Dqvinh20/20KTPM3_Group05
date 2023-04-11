const router = require("express").Router();
const upload = require("../utils/multer");

const PostController = require("../controllers/post.controller");
const PostService = require("../services/post.service");

router.get("/", PostController.getAll);

router.get("/:post_id", PostController.getPostById);

router.get("/of-user/:user_id", PostController.getPostsByUser);

router.post("/create", upload.single("cover_img"), PostController.createPost);
router.get("/create/example-post", PostController.createExamplePost);

router.patch("/update", upload.single("cover_img"), PostController.updatePost);

router.delete("/delete", PostController.deletePost);
router.get("/by-location/:location_id", PostController.getPostByIdLocation);

module.exports = router;
