package models

case class PracticeSession(
  sessionType: PracticeSessionType,
  numWords: Int, // the total number of words in the practice session
  duration: Int, // the duration of the practice session in seconds
  timestamp: Int, // the unix timestamp of when the practice session started
  didFinish: Boolean // a boolean indicating if the practice session was finished or was interrupted by the user
) extends CSVRepresentable

