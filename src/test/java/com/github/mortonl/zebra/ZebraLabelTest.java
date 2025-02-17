package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.LabelElement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ZebraLabelTest {

    private ZebraLabel zebraLabel;

    @BeforeEach
    public void setUp() {
        zebraLabel = new ZebraLabel();
    }

    /**
     * Test that an element is successfully added to the list of elements in ZebraLabel
     */
    @Test
    public void testAddElementSuccessfullyAddsElement() {
        // Arrange
        LabelElement mockElement = new LabelElement() {
            @Override
            public String toZplString() {
                return "MockElement";
            }
        };

        // Act
        zebraLabel.addElement(mockElement);

        // Assert
        assertEquals(1, zebraLabel.getElements().size());
        assertTrue(zebraLabel.getElements().contains(mockElement));
    }

    /**
     * Test adding multiple elements to ensure the list grows correctly.
     * This tests the normal behavior and checks if there's any unexpected limit.
     */
    @Test
    public void testAddElement_MultipleElements() {
        LabelElement element1 = new LabelElement() {
            @Override
            public String toZplString() {
                return "Element1";
            }
        };
        LabelElement element2 = new LabelElement() {
            @Override
            public String toZplString() {
                return "Element2";
            }
        };

        zebraLabel.addElement(element1);
        zebraLabel.addElement(element2);

        assertEquals(2, zebraLabel.getElements().size());
        assertEquals(element1, zebraLabel.getElements().get(0));
        assertEquals(element2, zebraLabel.getElements().get(1));
    }

    /**
     * Test adding a null element to the ZebraLabel.
     * This tests the scenario where the input is empty/invalid.
     */
    @Test
    public void testAddElement_NullElement() {
        assertThrows(NullPointerException.class, () -> zebraLabel.addElement(null));
    }

    /**
     * Test that toZplString() returns a valid ZPL string with start and end format codes
     * and includes ZPL representations of all added elements.
     */
    @Test
    public void testToZplStringReturnsValidZplString() {
        // Arrange
        LabelElement mockElement1 = new LabelElement() {
            @Override
            public String toZplString() {
                return "^FO100,100^A0N,50,50^FDTest Element 1^FS";
            }
        };
        LabelElement mockElement2 = new LabelElement() {
            @Override
            public String toZplString() {
                return "^FO200,200^A0N,40,40^FDTest Element 2^FS";
            }
        };

        zebraLabel.addElement(mockElement1);
        zebraLabel.addElement(mockElement2);

        // Act
        String result = zebraLabel.toZplString();

        // Assert
        String expected = ZplCommandCodes.START_FORMAT + "^FO100,100^A0N,50,50^FDTest Element 1^FS" + "^FO200,200^A0N,40,40^FDTest Element 2^FS" + ZplCommandCodes.END_FORMAT;
        assertEquals(expected, result);
    }

    /**
     * Test the toZplString method when an element throws an exception.
     * It should propagate the exception.
     */
    @Test
    public void testToZplString_ElementThrowsException() {
        LabelElement mockElement = Mockito.mock(LabelElement.class);
        when(mockElement.toZplString()).thenThrow(new RuntimeException("Test exception"));

        zebraLabel.addElement(mockElement);

        assertThrows(RuntimeException.class, zebraLabel::toZplString);
    }

    /**
     * Test the toZplString method when the elements list is empty.
     * It should return a string with only start and end format codes.
     */
    @Test
    public void testToZplString_EmptyElementsList() {
        String result = zebraLabel.toZplString();

        assertEquals(ZplCommandCodes.START_FORMAT + ZplCommandCodes.END_FORMAT, result);
    }

    /**
     * Test the toZplString method with a large number of elements.
     * It should handle the case without running out of memory.
     */
    @Test
    public void testToZplString_LargeNumberOfElements() {
        LabelElement mockElement = Mockito.mock(LabelElement.class);
        when(mockElement.toZplString()).thenReturn("MOCK");

        // Add a large number of elements
        for (int i = 0; i < 100000; i++) {
            zebraLabel.addElement(mockElement);
        }

        String result = zebraLabel.toZplString();

        assertTrue(result.startsWith(ZplCommandCodes.START_FORMAT));
        assertTrue(result.endsWith(ZplCommandCodes.END_FORMAT));
        assertEquals(100000 * 4 + ZplCommandCodes.START_FORMAT.length() + ZplCommandCodes.END_FORMAT.length(), result.length());
    }
}