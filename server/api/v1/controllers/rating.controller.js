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
    const { post_id } = req.params;
    let { limit, page } = req.query;

    if (!limit) limit = 10;
    if (!page) page = 1;
    page = Number(page);
    limit = Number(limit);

    const offset = (page - 1) * limit;

    const results = await RatingService.getAllRating(post_id, {
      limit,
      offset,
    });
    const { count, rows } = results;
    const maxPage = Math.ceil(count / limit);
    const ratings = {
      current_page: page,
      max_page: maxPage,
      next_page: page < maxPage ? page + 1 : null,
      prev_page: page > 1 ? page - 1 : null,
      rating_per_page: limit,
      data: rows,
    };
    return res.json({ success: 1, error: null, ratings });
  } catch (err) {
    return res.status(400).json({ success: 0, error: "Can't get all ratting" });
  }
};

module.exports = {
  createRating,
  getAllRating,
};
