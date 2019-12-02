import org.scalatest.FunSuite
import models._
import implicits._

class InterpolatorTests extends FunSuite {
  import Interpolators._

  test("conversion from word to CSV representation") {
    assertResult("ardor,enthusiasm or passion,noun,0") {
      Word("ardor", "enthusiasm or passion", Some(Noun), 0).toCSVRepr
    }
  }

  test("conversion from word to CSV representation no part of speech") {
    assertResult("ardent,enthusiastically or passionately,,100") {
      Word("ardent", "enthusiastically or passionately", None, 100).toCSVRepr
    }
  }
}
