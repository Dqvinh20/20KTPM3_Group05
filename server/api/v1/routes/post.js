const router = require("express").Router();
const upload = require("../utils/multer");

const PostController = require("../controllers/post.controller");
const PostService = require("../services/post.service");
const Validation = require("../utils/validation");
const { body, param } = require("express-validator");

router.get("/", PostController.getAll);

router.get("/:post_id", PostController.getPostById);

router.get("/of-user/:user_id", PostController.getPostsByUser);

router.patch(
  "/change-trip-dates/:post_id",
  param("post_id")
    .notEmpty()
    .withMessage("Post Id is required")
    .isInt()
    .withMessage("Post Id must be an integer")
    .custom((value) => {
      return PostService.getPostById(value).then((post) => {
        if (!post) {
          return Promise.reject("Post not found");
        }
      });
    }),
  Validation.validate,
  PostController.changeTripDates
);

router.patch(
  "/increase-view",
  body("post_id")
    .notEmpty()
    .withMessage("Post Id is required")
    .isInt()
    .withMessage("Post Id must be an integer")
    .custom((value) => {
      return PostService.getPostById(value).then((post) => {
        if (!post) {
          return Promise.reject("Post not found");
        }
      });
    }),
  Validation.validate,
  PostController.increaseView
);

router.patch(
  "/decrease-view",
  body("post_id")
    .notEmpty()
    .withMessage("Post Id is required")
    .isInt()
    .withMessage("Post Id must be an integer")
    .custom((value) => {
      return PostService.getPostById(value).then((post) => {
        if (!post) {
          return Promise.reject("Post not found");
        }
      });
    }),
  Validation.validate,
  PostController.decreaseView
);

router.post("/create", upload.single("cover_img"), PostController.createPost);
router.get("/create/example-post", PostController.createExamplePost);

router.patch("/update", upload.single("cover_img"), PostController.updatePost);

router.delete("/delete", PostController.deletePost);
router.get("/by-location/:location_id", PostController.getPostByIdLocation);

module.exports = router;
