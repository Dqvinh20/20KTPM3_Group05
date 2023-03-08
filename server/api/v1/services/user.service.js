const User = require("../models/user.model");

const getUserByEmail = async (email) => {
    console.log("email: " + email);
    return await User.findOne({ where: { email } });
};

const createUser = async (user) => {
    return await User.create(user);
};

module.exports = {
    getUserByEmail,
    createUser,
};
