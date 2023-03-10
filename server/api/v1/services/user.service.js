const User = require("../models/user.model");
const sequelize = require("../config");
const getUserByEmail = async (email) => {
    console.log("email: " + email);
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
            attributes: ["id", "email", "avatar"],
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
            attributes: ["id", "email", "avatar"],
        },
    });
};

const getUserInfo = async (user_id) => {
    return await User.findOne({
        where: {
            id: user_id,
        },
        attributes: ["id", "email", "followers_count", "following_count"],
    });
};
const incressFollowing = async (user_id) => {
    return await User.update(
        { following_count: sequelize.literal("following_count + 1") },
        { where: { id: user_id } }
    );
};
const decressFollowing = async (user_id) => {
    return await User.update(
        { following_count: sequelize.literal("following_count - 1") },
        { where: { id: user_id } }
    );
};
const decressFollower = async (user_id) => {
    return await User.update(
        { followers_count: sequelize.literal("followers_count - 1") },
        { where: { id: user_id } }
    );
};
const incressFollower = async (user_id) => {
    return await User.update(
        { followers_count: sequelize.literal("followers_count + 1") },
        { where: { id: user_id } }
    );
};

const followUser = async (user_id, following_id) => {
    const existingFollow = await sequelize.models.user_followers.findOne({
        where: {
            follower_id: user_id,
            following_id: following_id,
        },
    });
    if (existingFollow) {
        return { error: "User already followed" };
    }
    incressFollowing(user_id);
    incressFollower(following_id);

    return await sequelize.models.user_followers.create({
        follower_id: user_id,
        following_id: following_id,
    });
};
const unfollowUser = async (user_id, following_id) => {
    const existingFollow = await sequelize.models.user_followers.findOne({
        where: {
            follower_id: user_id,
            following_id: following_id,
        },
    });
    if (!existingFollow) {
        return { error: "User already unfollowed" };
    }
    decressFollowing(user_id);
    decressFollower(following_id);
    return existingFollow.destroy();
};

module.exports = {
    getUserByEmail,
    createUser,
    getFollowers,
    getFollowings,
    getUserInfo,
    followUser,
    incressFollowing,
    decressFollowing,
    decressFollower,
    incressFollower,
    unfollowUser,
};
