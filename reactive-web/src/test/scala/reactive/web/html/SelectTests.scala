package reactive
package web
package html

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import net.liftweb.mockweb._

class SelectTests extends FunSuite with ShouldMatchers with Observing {
  def withNewPage[T](p: => T): T = Page.withPage(new Page)(p)
  implicit val config = Config.defaults

  test("Selection should initally be defined") {
    val select = Select(Val(List("A", "B")))
    select.selectedIndex.now.isDefined should be(true)
  }

  test("Creating an empty Select should not IndexOutOfBounds") {
    Select(Val(Nil))
    ()
  }

  test("When selectedIndex is None then selectedItem is None") {
    val select = Select(Val(List("A", "B")))
    select.selectedIndex() = None
    select.selectedItem.now should equal(None)
  }

  test("Replacing items should cause selectedItem to change") {
    MockWeb.testS("/") {
      val items = Var(List("A", "B"))
      val select = Select(items)
      select.render

      select.items.now should equal(items.now)
      select.selectedItem.now should equal(Some("A"))
      select.selectedIndex.now should equal(Some(0))

      println("Updating")
      items() = List("C", "D")
      select.items.now should equal(items.now)
      select.selectedItem.now should equal(Some("C"))
      select.selectedIndex.now should equal(Some(0))

      items() = List("E", "F")
      select.items.now should equal(items.now)
      select.selectedItem.now should equal(Some("E"))
      select.selectedIndex.now should equal(Some(0))

      items() = List("E", "F")
      select.items.now should equal(items.now)
      select.selectedItem.now should equal(Some("E"))
      select.selectedIndex.now should equal(Some(0))
    }
  }

  test("Replacing items maintains the correct selection") {
    val itemsA = List("N", "B", "T")
    val itemsB = List("N", "B", "T", "K")
    val v = Var(itemsA)
    val select = Select(v)
    select.selectedItem () = Some("N")
    v () = itemsB
    select.selectedItem.now should equal (Some("N"))
  }
}
