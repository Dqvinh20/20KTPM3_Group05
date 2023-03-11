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
    const user_id = req.user.id; // lấy id của bản thân người dùng
    const following_id = req.params.user_id; // lấy id của người  mà người dùng muốn follow
    try {
        const user = await UserService.followUser(user_id, following_id);
        console.log("User" + user);
        res.json(user);
    } catch (error) {
        res.json(error);
    }
};
const unfollowUser = async (req, res) => {
    const user_id = req.user.id; // lấy id của bản thân người dùng
    const following_id = req.params.user_id; // lấy id của người  mà người dùng muốn follow
    try {
        const user = await UserService.unfollowUser(user_id, following_id);
        res.json(user);
    } catch (error) {
        res.json(error);
    }
};
const likePost = async (req, res) => {
    const user_id = req.user.id;
    const post_id = req.params.post_id;
    try {
        const post = await UserService.likePost(user_id, post_id);
        res.json(post);
    } catch (error) {
        res.json(error);
    }
};
const unlikePost = async (req, res) => {
    const user_id = req.user.id;
    const post_id = req.params.post_id;
    try {
        const post = await UserService.unlikePost(user_id, post_id);
        res.json(post);
    } catch (error) {
        res.json(error);
    }
};

module.exports = {
    getFollowers,
    getFollowings,
    getUserInfo,
    followUser,
    unfollowUser,
    likePost,
    unlikePost,
};
