const RatingService = require("../services/rating.service");

const createRating = async (req, res) => {
    try {
        const { post_id, score, content } = req.body;
        const ratingData = {
            score,
            content,
            post_id,
            rating_user_id: req.user.id,
        };
        const rating = await RatingService.createRating(ratingData);
        res.json(rating);
    } catch (err) {
        res.json(err);
    }
};

const getAllRating = async (req, res) => {
    try {
        const ratings = await RatingService.getAllRating(req.params.post_id);
        res.json(ratings);
    } catch (err) {
        res.json(err);
    }
};
module.exports = {
    createRating,
    getAllRating,
};
