const router = require("express").Router();
const { body } = require("express-validator");

const Validation = require("../utils/validation");
const ScheduleController = require("../controllers/schedule.controller");

const requiredBodyValidator = [
  body("schedule_id")
    .notEmpty()
    .withMessage("Schedule Id is required")
    .bail()
    .isInt()
    .withMessage("Schedule Id must be an integer"),
  body("location_id")
    .notEmpty()
    .withMessage("Location Id is required")
    .bail()
    .isInt()
    .withMessage("Location Id must be an integer"),
];

router.get("/", ScheduleController.getAll);

router.get("/:id", ScheduleController.getScheduleById);

router.post(
  "/add-location",
  requiredBodyValidator,
  Validation.validate,
  ScheduleController.addLocation
);

router.delete(
  "/remove-location",
  body("schedule_id")
    .notEmpty()
    .withMessage("Schedule Id is required")
    .bail()
    .isInt()
    .withMessage("Schedule Id must be an integer"),
  body("location_pos")
    .notEmpty()
    .withMessage("Location position is required")
    .bail()
    .isInt()
    .withMessage("Location position must be an integer"),
  Validation.validate,
  ScheduleController.removeLocation
);

router.patch(
  "/edit-location-note",
  body("schedule_id")
    .notEmpty()
    .withMessage("Schedule Id is required")
    .bail()
    .isInt()
    .withMessage("Schedule Id must be an integer"),
  body("location_pos")
    .notEmpty()
    .withMessage("Location position is required")
    .bail()
    .isInt()
    .withMessage("Location position must be an integer"),
  body("note").notEmpty().withMessage("Note is required"),
  Validation.validate,
  ScheduleController.editLocationNote
);

module.exports = router;
