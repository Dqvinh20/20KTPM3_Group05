const UserService = require("../services/user.service");

const getFollowers = async (req, res) => {
    try {
        const followers = await UserService.getFollowers(req.params.user_id);
        res.json(followers);
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

module.exports = {
    getFollowers,
    getFollowings,
};
