const RattingService = require("../services/rating.service");

const createRating = async (req, res) => {
    try {
        const { post_id, score, content } = req.body;
        const ratingData = {
            score,
            content,
            post_id,
            rating_user_id: req.user.id,
        };
        console.log(ratingData);
        const rating = await RattingService.createRating(ratingData);
        res.json(rating);
    } catch (err) {
        res.json(err);
    }
};

module.exports = {
    createRating,
};
