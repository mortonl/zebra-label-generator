package com.github.mortonl.zebra.elements;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "createPositionedAndSizedElement", setterPrefix = "with")
public class PositionedAndSizedElement extends PositionedElement
{
    protected Double widthMm;
    protected Double heightMm;

    public static abstract class PositionedAndSizedElementBuilder<C extends PositionedAndSizedElement, B extends PositionedAndSizedElementBuilder<C, B>>
        extends PositionedElementBuilder<C, B>
    {
        public B withSize(double widthMm, double heightMm)
        {
            this.widthMm = widthMm;
            this.heightMm = heightMm;
            return self();
        }
    }
}
