package view

import model.Field
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MouseClickListener(
    private val field: Field,
    private val buttonLeft: (Field) -> Unit,
    private val buttonRight: (Field) -> Unit
) : MouseListener {


    override fun mousePressed(event: MouseEvent?) {
        when (event?.button) {
            MouseEvent.BUTTON1 -> buttonLeft(field);
            MouseEvent.BUTTON3 -> buttonRight(field)
        }
    }

    override fun mouseClicked(event: MouseEvent?) {}
    override fun mouseReleased(event: MouseEvent?) {}
    override fun mouseEntered(event: MouseEvent?) {}
    override fun mouseExited(event: MouseEvent?) {}
}