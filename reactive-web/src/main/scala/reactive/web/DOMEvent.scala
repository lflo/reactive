package reactive
package web

import javascript._
import net.liftweb.http.S

/**
 * Implicit instances of EventEncoder are associated with DOMEvents
 * to provide a javascript object initializer expression
 * that consists of the relevant data in the browser's Event object.
 */
class EventEncoder[T<:DOMEvent](val encodeExp: $[JsTypes.JsObj])

object EventEncoder {
  val empty = Map.empty.$
  implicit val blur = new EventEncoder[Blur](empty)
  implicit val change = new EventEncoder[Change](empty)
  implicit val error = new EventEncoder[Error](empty)
  implicit val focus = new EventEncoder[Focus](empty)
  implicit val resize = new EventEncoder[Resize](empty)
  implicit val unload = new EventEncoder[Unload](empty)

  val modifiers = Map[String,$[JsTypes.JsAny]](
    "alt"->   ('event.$->'altKey.$),
    "ctrl"->  ('event.$->'ctrlKey.$),
    "shift"-> ('event.$->'shiftKey.$),
    "meta"->  ('event.$->'metaKey.$)
  )
  val modifiersOnly = Map[String,$[JsTypes.JsAny]]("modifiers" -> modifiers.$)
  implicit val click = new EventEncoder[Click](modifiersOnly.$)
  implicit val dblClick = new EventEncoder[DblClick](modifiersOnly.$)
  implicit val selectText = new EventEncoder[SelectText](modifiersOnly.$)
  
  val key = Map[String,$[JsTypes.JsAny]](
    "code" -> ( ('event.$->'keyCode.$) || ('event.$->'charCode.$) ),
    "modifiers" -> modifiers.$
  )
  implicit val keyDown = new EventEncoder[KeyDown](key.$)
  implicit val keyUp = new EventEncoder[KeyUp](key.$)
  implicit val keyPress = new EventEncoder[KeyPress](key.$)
  
  def buttons: Map[String,$[JsTypes.JsAny]] = if(S.request.dmap(false)(_.isIE)) Map(
    "left" -> (('event.$->'buttons.$) & 1.$ !== 0.$),
    "middle" -> (('event.$->'buttons.$) & 4.$ !== 0.$),
    "right" -> (('event.$->'buttons.$) & 2.$ !== 0.$),
    "modifiers" -> modifiers.$
  ) else Map(
    "left" -> (('event.$->'buttons.$) === 0.$),
    "middle" -> (('event.$->'buttons.$) === 1.$),
    "right" -> (('event.$->'buttons.$) === 2.$),
    "modifiers" -> modifiers.$
  )
  def mouse: Map[String,$[JsTypes.JsAny]] = Map(
    "buttons" -> buttons.$,
    "pos" -> Map[String,$[JsTypes.JsAny]](
      "x" -> ('event.$->'clientX.$),
      "y" -> ('event.$->'clientY.$)
    ).$
  )
  implicit def mouseDown = new EventEncoder[MouseDown](mouse.$) 
  implicit def mouseUp = new EventEncoder[MouseUp](mouse.$) 
  implicit def mouseMove = new EventEncoder[MouseMove](mouse.$)
  
  implicit def mouseOut = new EventEncoder[MouseOut](mouse.$)  //TODO related 
  implicit def mouseOver = new EventEncoder[MouseOver](mouse.$)  //TODO related
}

/**
 * The base class for all events
 */
sealed trait DOMEvent
case class Blur() extends DOMEvent
case class Change() extends DOMEvent
case class Click(modifiers: Modifiers) extends DOMEvent
case class DblClick(modifiers: Modifiers) extends DOMEvent
case class Error() extends DOMEvent
case class Focus() extends DOMEvent
/**
 * @param code the keyCode or charCode property of the javascript event object
 */
case class KeyDown(code: Int, modifiers: Modifiers) extends DOMEvent
/**
 * @param code the keyCode or charCode property of the javascript event object
 */
case class KeyPress(code: Int, modifiers: Modifiers) extends DOMEvent
/**
 * @param code the keyCode or charCode property of the javascript event object
 */
case class KeyUp(code: Int, modifiers: Modifiers) extends DOMEvent

case class MouseDown(buttons: Buttons, pos: Position) extends DOMEvent
case class MouseMove(buttons: Buttons, pos: Position) extends DOMEvent
/**
 * @param related the RElem corresponding to the relatedTarget or toElement property of the event object
 */
case class MouseOut(buttons: Buttons, pos: Position, related: Option[RElem]) extends DOMEvent
/**
 * @param related the RElem corresponding to the relatedTarget or fromElement property of the event object
 */
case class MouseOver(buttons: Buttons, pos: Position, related: Option[RElem]) extends DOMEvent
case class MouseUp(buttons: Buttons, pos: Position) extends DOMEvent
case class Resize() extends DOMEvent
case class SelectText(modifiers: Modifiers) extends DOMEvent
case class Unload() extends DOMEvent

/**
 * Encapsulates the state of modifier keys on the keyboard at the time of this event
 */
case class Modifiers(alt: Boolean, ctrl: Boolean, shift: Boolean, meta: Boolean)
/**
 * Encapsulates the mouse buttons used to generate the event along with the modifier keys
 */
case class Buttons(left: Boolean, middle: Boolean, right: Boolean, modifiers: Modifiers)
/**
 * Encapsulates a mouse pointer position
 */
//TODO remove? just use Tuple2?
case class Position(x: Int, y: Int)
