const router = require("express").Router();
const Passport = require("./utils/passport");

require("./models");

router.use("/ping", async (req, res) => {
  return res.json({
    greeting: "Hello from TripShare API v1",
    date: new Date(),
    url: req.protocol + "://" + req.get("host") + req.originalUrl,
    headers: Object.assign({}, req.headers),
  });
});
router.use("/auth", require("./routes/auth"));
router.use("/home", Passport.isAuth, require("./routes/home"));
router.use("/search", Passport.isAuth, require("./routes/search"));
router.use("/user", Passport.isAuth, require("./routes/user"));
router.use("/post", Passport.isAuth, require("./routes/post"));
router.use("/schedule", Passport.isAuth, require("./routes/schedule"));
router.use("/location", Passport.isAuth, require("./routes/location"));
router.use("/rating", Passport.isAuth, require("./routes/rating"));

module.exports = router;
