const Rating = require("../models/rating.model");
const Post = require("../models/post.model");
const { Op } = require("sequelize");

const createRating = async (ratingData) => {
  try {
    const newRating = await Rating.create(ratingData);
    const ratings = await Rating.findAndCountAll({
      attributes: [
        [
          Rating.sequelize.fn("AVG", Rating.sequelize.col("score")),
          "avg_rating",
        ],
      ],
      where: {
        post_id: ratingData.post_id,
      },
    });

    const post = await Post.findByPk(ratingData.post_id);
    await post.setDataValue(
      "avg_rating",
      ratings.rows[0].dataValues.avg_rating
    );
    await post.setDataValue("rating_count", ratings.count);
    await post.save();
    return { success: 1, error: null, newRating };
  } catch (error) {
    console.log(error);
    return { success: 0, error: "Can't create ratting" };
  }
};

const getAllRating = async (post_id, options) => {
  let rating = await Rating.findAndCountAll({
    where: {
      post_id: {
        [Op.eq]: post_id,
      },
    },
    attributes: {
      exclude: ["post_id"],
    },
    order: [["createdAt", "DESC"]],
    ...options,
  });
  return rating;
};
module.exports = {
  createRating,
  getAllRating,
};
