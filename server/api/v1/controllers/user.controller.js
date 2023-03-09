const UserService = require("../services/user.service");

const getFollowers = async (req, res) => {
    try {
        const followers = await UserService.getFollowers(req.params.user_id);
        res.json(followers);
    } catch (error) {
        res.json(error);
    }
};
const getUserInfo = async (req, res) => {
    try {
        const user = await UserService.getUserInfo(req.params.user_id);
        res.json(user);
    } catch (error) {
        res.json(error);
    }
};
const getFollowings = async (req, res) => {
    try {
        const followings = await UserService.getFollowings(req.params.user_id);
        res.json(followings);
    } catch (error) {
        res.json(error);
    }
};
const followUser = async (req, res) => {
    try {
        const user = await UserService.followUser(
            req.params.user_id,
            req.body.following_id
        );
        res.json(user);
    } catch (error) {
        res.json(error);
    }
};
module.exports = {
    getFollowers,
    getFollowings,
    getUserInfo,
    followUser,
};
