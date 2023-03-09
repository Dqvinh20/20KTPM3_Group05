const User = require("../models/user.model");

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

const followUser = async (user_id, following_id) => {};

module.exports = {
    getUserByEmail,
    createUser,
    getFollowers,
    getFollowings,
    getUserInfo,
};
