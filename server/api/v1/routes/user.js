const express = require("express");
const router = express.Router();
const UserController = require("../controllers/user.controller");

router.get("/:user_id/followers", UserController.getFollowers);
router.get("/:user_id/followings", UserController.getFollowings);
router.get("/:user_id", UserController.getUserInfo);
router.post("/:user_id/follow", UserController.followUser);
router.post("/:user_id/unfollow", UserController.unfollowUser);
module.exports = router;
