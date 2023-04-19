const User = require("../models/user.model");
const sequelize = require("../config");
const PostService = require("../services/post.service");

const getUserByEmail = async (email) => {
  return await User.findOne({ where: { email } });
};

const getUserByUsername = async (user_name) => {
  return await User.findOne({ where: { user_name } });
};

const createUser = async (user) => {
  return await User.create(user);
};

const getFollowers = async (user_id) => {
  return await User.findAll({
    where: {
      id: user_id,
    },
    include: {
      association: "followers",
      attributes: ["id", "user_name", "email", "avatar"],
    },
  });
};

const getFollowings = async (user_id) => {
  return await User.findAll({
    where: {
      id: user_id,
    },
    include: {
      association: "followings",
      attributes: ["id", "user_name", "email", "avatar"],
    },
  });
};

const getUserInfo = async (user_id) => {
  return await User.findOne({
    where: {
      id: user_id,
    },
    attributes: [
      "id",
      "user_name",
      "name",
      "name_non_accent",
      "email",
      "avatar",
      "followers_count",
      "following_count",
    ],
  });
};

const increaseFollowing = async (user_id) => {
  // người dùng khi follow thì tăng số lượng người mà người dùng đó đang follow lên 1
  return await User.update(
    { following_count: sequelize.literal("following_count + 1") },
    { where: { id: user_id } }
  );
};

const decreaseFollowing = async (user_id) => {
  // người dùng khi unfollow thì giảm số lượng người mà người dùng đó đang follow xuống 1
  return await User.update(
    { following_count: sequelize.literal("following_count - 1") },
    { where: { id: user_id } }
  );
};

const decreaseFollower = async (user_id) => {
  // người dùng khi unfollow thì giảm số lượng người mà người dùng đó đang follow xuống 1
  return await User.update(
    { followers_count: sequelize.literal("followers_count - 1") },
    { where: { id: user_id } }
  );
};

const increaseFollower = async (user_id) => {
  // người dùng khi follow thì tăng số lượng người mà người dùng đó đang follow lên 1
  return await User.update(
    { followers_count: sequelize.literal("followers_count + 1") },
    { where: { id: user_id } }
  );
};

const followUser = async (user_id, following_id) => {
  if (user_id == following_id) {
    return { error: "User cannot follow themselves" };
  }
  const existingFollow = await sequelize.models.user_followers.findOne({
    where: {
      follower_id: user_id,
      following_id: following_id,
    },
  });
  if (existingFollow) {
    return { success: 0, error: "User already followed" };
  }
  increaseFollowing(user_id);
  increaseFollower(following_id);

  const result = await sequelize.models.user_followers.create({
    follower_id: user_id,
    following_id: following_id,
  });
  return { success: 1, error: null, result };
};

const unfollowUser = async (user_id, following_id) => {
  if (user_id == following_id) {
    return { error: "User cannot follow themselves" };
  }
  const existingFollow = await sequelize.models.user_followers.findOne({
    where: {
      follower_id: user_id,
      following_id: following_id,
    },
  });
  if (!existingFollow) {
    return { success: 0, error: "User already unfollowed" };
  }
  decreaseFollowing(user_id);
  decreaseFollower(following_id);
  const result = existingFollow.destroy();
  return { success: 1, error: null };
};

const likePost = async (user_id, post_id) => {
  const existingLike = await sequelize.models.users_posts_like.findOne({
    where: {
      user_id: user_id,
      post_id: post_id,
    },
  });
  if (existingLike) return { success: 0, error: "The user liked the post" };
  PostService.increaseLikePost(post_id);
  const result = await sequelize.models.users_posts_like.create({
    user_id: user_id,
    post_id: post_id,
  });
  return { success: 1, error: null, result };
};

const unlikePost = async (user_id, post_id) => {
  const existingLike = await sequelize.models.users_posts_like.findOne({
    where: {
      user_id: user_id,
      post_id: post_id,
    },
  });
  if (!existingLike) return { success: 0, error: "The user not like the post" };
  PostService.decreaseLikePost(post_id);
  const result = existingLike.destroy();
  return { success: 1, error: null };
};

const updateUser = async (user_id, user) => {
  const result = await User.update(user, {
    where: { id: user_id },
    returning: true,
    individualHooks: true,
    attributes: [
      "id",
      "user_name",
      "name",
      "name_non_accent",
      "email",
      "avatar",
      "followers_count",
      "following_count",
    ],
  });

  return [result[0], result[1][0]];
};

const updatePassword = async (email, password) => {
  return await User.update(
    {
      password: password,
    },
    { where: { email: email } }
  );
};

module.exports = {
  getUserByEmail,
  getUserByUsername,
  createUser,
  getFollowers,
  getFollowings,
  getUserInfo,
  followUser,
  increaseFollowing,
  decreaseFollowing,
  decreaseFollower,
  increaseFollower,
  unfollowUser,
  likePost,
  unlikePost,
  updateUser,
  updatePassword,
};
