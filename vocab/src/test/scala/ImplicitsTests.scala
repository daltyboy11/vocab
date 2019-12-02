import org.scalatest.FunSuite
import models._
import implicits._

class ImplicitsTests extends FunSuite {
  import Implicits._

  test("conversion from word to CSV representation") {
    assertResult("ardor,enthusiasm or passion,noun,0") {
      Word("ardor", "enthusiasm or passion", Some(Noun), 0).toCSVRepr
    }
  }

  test("conversion from CSV representation to Word with part of speech") {
    assertResult(Word("ardor", "enthusiasm or passion", Some(Noun), 0)) {
      "ardor,enthusiasm or passion,noun,0".toWord
    }
  }

  test("conversion from CSV representation to Word without part of speech") {
    assertResult(Word("ardor", "enthusiasm or passion", None, 0)) {
      "ardor,enthusiasm or passion,,0".toWord
    }
  }

  test("conversion from word to CSV representation no part of speech") {
    assertResult("ardent,enthusiastically or passionately,,100") {
      Word("ardent", "enthusiastically or passionately", None, 100).toCSVRepr
    }
  }

  test("conversion from practice session to CSV representation") {
    assertResult("all,10,1000,123456789,true") {
      PracticeSession(All, 10, 1000, 123456789, true).toCSVRepr
    }
    assertResult("half,10,1000,123456789,false") {
      PracticeSession(Half, 10, 1000, 123456789, false).toCSVRepr
    }
    assertResult("100,10,1000,123456789,true") {
      PracticeSession(ExplicitNumeric(100), 10, 1000, 123456789, true).toCSVRepr
    }
    assertResult("0.5,10,1000,123456789,false") {
      PracticeSession(PercentageNumeric(0.5f), 10, 1000, 123456789, false).toCSVRepr
    }
  }

  test("convertsion from CSV representation to practice session") {
    assertResult(PracticeSession(All, 10, 1000, 123456789, true)) {
      "all,10,1000,123456789,true".toPracticeSession
    }
  }
}
