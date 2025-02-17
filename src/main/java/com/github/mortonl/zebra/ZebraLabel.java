package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.LabelElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ZebraLabel {

    private final List<LabelElement> elements;

    public ZebraLabel() {
        this.elements = new ArrayList<LabelElement>();
    }

    public void addElement(LabelElement element) {
        elements.add(element);
    }

    public String toZplString() {
        StringBuilder zpl = new StringBuilder();

        // Add start format
        zpl.append(ZplCommandCodes.START_FORMAT);

        // Add each element's ZPL representation
        for (LabelElement element : elements) {
            zpl.append(element.toZplString());
        }

        // Add end format
        zpl.append(ZplCommandCodes.END_FORMAT);

        return zpl.toString();
    }
}
