import React from "react";
import PropTypes from "prop-types";

const ValidationError = ({ text }) => <span style={{ color: "red" }}>{text}</span>;

ValidationError.propTypes = {
  text: PropTypes.string.isRequired
};

export default ValidationError;
