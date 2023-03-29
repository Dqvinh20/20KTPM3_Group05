const Rating = require("../models/rating.model");
const Post = require("../models/post.model");
const sequelize = require("../config");

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
        return rating;
    } catch (error) {
        console.log(error);
    }
};

module.exports = {
    createRating,
};
