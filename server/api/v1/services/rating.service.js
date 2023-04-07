const Rating = require("../models/rating.model");
const Post = require("../models/post.model");
const sequelize = require("../config");
const { Op } = require("sequelize");
const getMethods = (obj) => {
    let properties = new Set();
    let currentObj = obj;
    do {
        Object.getOwnPropertyNames(currentObj).map((item) =>
            properties.add(item)
        );
    } while (
        (currentObj = Object.getPrototypeOf(currentObj)) &&
        Object.getPrototypeOf(currentObj)
    );

    return [...properties.keys()].filter(
        (item) => typeof obj[item] === "function"
    );
};

const createRating = async (ratingData) => {
    try {
        const rating = await Rating.create(ratingData);
        const post = await Post.findByPk(ratingData.post_id);
        await post.increment("rating_count", { by: 1 });
        post.avg_rating =
            (post.avg_rating * (post.rating_count - 1) + ratingData.score) /
            post.rating_count;
        await post.save();
        return { success: 1, error: null, rating };
    } catch (error) {
        console.log(error);
        return { success: 0, error: "Can't create ratting" };
    }
};

const getAllRating = async (post_id) => {
    try {
        console.log(post_id);
        const ratting = await Rating.findAll({
            where: {
                post_id: {
                    [Op.eq]: post_id,
                },
            },
            attributes: ["id", "score", "content", "rating_user_id"],
        });
        return { success: 1, error: null, ratting };
    } catch (error) {
        console.log(error);
        return { success: 0, error: "Can't get all ratting" };
    }
};
module.exports = {
    createRating,
    getAllRating,
};
