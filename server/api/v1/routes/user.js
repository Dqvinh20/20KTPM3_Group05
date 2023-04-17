const express = require("express");
const router = express.Router();
const { body, validationResult } = require("express-validator");

const UserController = require("../controllers/user.controller");
const UserService = require("../services/user.service");

const multer = require("../utils/multer");

router.get("/:user_id/followers", UserController.getFollowers);
router.get("/:user_id/followings", UserController.getFollowings);
router.get("/:user_id", UserController.getUserInfo);
router.post("/:user_id/follow", UserController.followUser);
router.delete("/:user_id/unfollow", UserController.unfollowUser);
router.post("/like/:post_id", UserController.likePost);
router.delete("/unlike/:post_id", UserController.unlikePost);
router.patch(
  "/update",
  multer.single("avatar_img"),
  body("user_name").custom(async (value) => {
    const user = await UserService.getUserByUsername(value);
    if (user) {
      return Promise.reject("Username already exists");
    }
  }),
  (req, res, next) => {
    const errors = validationResult(req);

    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    next();
  },
  UserController.updateUser
);
module.exports = router;

// TODO:

//DONE
//PATCH /update/:user_id: Cập   nhật thông tin cá nhân
//POST /reset-password: Đặt lại mật khẩu
//GET /like/:post_id: Thích bài viết
//GET /un-like/:post_id: Bỏ like bài viết
//GET /:user_id: Lấy thông tin cá nhân
//GET /:user_id/follower: Lấy danh sách ng theo dõi
//GET /:user_id/following: Lấy danh sách người đang theo dõi
//POST /follow/user_id: Theo dõi
//POST /un-follow/user_id: Bỏ theo dõi
