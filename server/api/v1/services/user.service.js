const User = require("../models/user.model");
const sequelize = require("../config");
const PostService = require("../services/post.service");
const { where } = require("sequelize");

const getUserByEmail = async (email) => {
    return await User.findOne({ where: { email } });
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
            "user_name_non_accent",
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
        return { error: "User already followed" };
    }
    increaseFollowing(user_id);
    increaseFollower(following_id);

    return await sequelize.models.user_followers.create({
        follower_id: user_id,
        following_id: following_id,
    });
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
        return { error: "User already unfollowed" };
    }
    decreaseFollowing(user_id);
    decreaseFollower(following_id);
    return existingFollow.destroy();
};

const likePost = async (user_id, post_id) => {
    const existingLike = await sequelize.models.users_posts_like.findOne({
        where: {
            user_id: user_id,
            post_id: post_id,
        },
    });
    if (existingLike) return { error: "The user liked the post" };
    PostService.increaseLikePost(post_id);
    return await sequelize.models.users_posts_like.create({
        user_id: user_id,
        post_id: post_id,
    });
};

const unlikePost = async (user_id, post_id) => {
    const existingLike = await sequelize.models.users_posts_like.findOne({
        where: {
            user_id: user_id,
            post_id: post_id,
        },
    });
    if (!existingLike) return { error: "The user not like the post" };
    PostService.decreaseLikePost(post_id);
    return existingLike.destroy();
};

const updateUser = async (user_id, user_name, avatar_url) => {
    return await User.update(
        {
            user_name: user_name,
            avatar: avatar_url,
        },

        {
            where: {
                id: user_id,
            },
        }
    );
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
