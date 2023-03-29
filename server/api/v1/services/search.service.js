const { Op } = require("sequelize");

const Post = require("../models/post.model");
const User = require("../models/user.model");
const Location = require("../models/location.model");

const commonPost = {
    attributes: { exclude: ["created_by", "title_non_accent"] },
};

const commonUser = {
    attributes: ["id", "user_name", "user_name_non_accent", "email", "avatar"],
};

const commonLocation = {};

const searchPost = async (query) => {
    const result = await Post.findAll({
        ...commonPost,
        where: {
            [Op.and]: [
                Post.sequelize.literal(
                    "_search @@ plainto_tsquery('english', :query)"
                ),
                { is_public: true },
            ],
        },

        replacements: { query },
    });
    return result;
};

const searchUser = async (query, { limit }) => {
    const result = await User.findAll({
        limit,
        ...commonUser,
        where: {
            [Op.or]: [
                User.sequelize.literal(
                    "_search @@ plainto_tsquery('english', :query)"
                ),
                {
                    [Op.or]: [
                        User.sequelize.where(
                            User.sequelize.fn(
                                "lower",
                                User.sequelize.col("user_name")
                            ),
                            Op.like,
                            User.sequelize.fn("lower", `%${query}%`)
                        ),
                        User.sequelize.where(
                            User.sequelize.fn(
                                "lower",
                                User.sequelize.col("user_name_non_accent")
                            ),
                            Op.like,
                            User.sequelize.fn("lower", `%${query}%`)
                        ),
                    ],
                },
            ],
        },
        replacements: { query },
    });
    return result;
};

const searchLocation = async (query, { limit }) => {
    const result = await Location.findAll({
        limit,
        ...commonLocation,
        where: {
            [Op.or]: [
                Location.sequelize.literal(
                    "_search @@ plainto_tsquery('english', :query)"
                ),

                Location.sequelize.where(
                    Location.sequelize.fn(
                        "lower",
                        Location.sequelize.col("name")
                    ),
                    Op.like,
                    Location.sequelize.fn("lower", `%${query}%`)
                ),
            ],
        },
        replacements: { query },
    });
    return result;
};

module.exports = {
    searchPost,
    searchUser,
    searchLocation,
};
