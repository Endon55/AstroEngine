package com.anthonycosenza.rendering.text.tables.types;

import com.anthonycosenza.math.vector.Vector2i;
import com.anthonycosenza.rendering.text.FontData;
import com.anthonycosenza.rendering.text.tables.types.hints.Hint;
import com.anthonycosenza.rendering.text.tables.types.points.CurvedPoint;
import com.anthonycosenza.rendering.text.tables.types.points.FontPoint;
import com.anthonycosenza.rendering.text.tables.types.points.StraightPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GlyphPath
{
    private Vector2i point;
    private Stack<Number> stack;
    private int width;
    private boolean hasWidth;
    private int totalValues;
    private int hintMask;
    private int stems;
    private String previousOperator;
    
    List<FontPoint> points;
    List<Hint> hints;
    private int defaultWidth;
    private int nominalWidth;
    
    public GlyphPath(FontData fontData)
    {
        stack = new Stack<>();
        point = new Vector2i(0, 0);
        points = new ArrayList<>();
        hints = new ArrayList<>();
        stems = 0;
        hintMask = 0;
        width = 0;
        hasWidth = false;
        
        List<Number> defWid = fontData.cffPrivateDict.getValue("defaultWidthX");
        List<Number> nomWid = fontData.cffPrivateDict.getValue("nominalWidthX");
        if(defWid != null && !defWid.isEmpty())
        {
            defaultWidth = defWid.get(0).intValue();
            width = defaultWidth;
            hasWidth = true;
        }
        else defaultWidth = 0;
        if(nomWid != null && !nomWid.isEmpty())
        {
            nominalWidth = nomWid.get(0).intValue();
        }
        else nominalWidth = 0;
        
    }
    
    public void setHintMask(int hintMask)
    {
        this.hintMask = hintMask;
    }
    
    public int getHintCount()
    {
        return hints.size();
    }
    
    public void pushValue(int value)
    {
        stack.push(value);
        System.out.println("Pushed Value, Stack: " + stack);
        totalValues++;
    }
    
    public void pushOperator(int operator1, int operator2, FontData fontData)
    {
        if(operator1 == 247)
        {
            System.out.println("10 shouldnt be here");
        }
        String operatorStr = switch(operator1)
                {
                    case 0, 2, 9, 13, 15, 16, 17 -> throw new RuntimeException("Reserved 1-byte CharString operator: " + operator1);
                    

                    /*case 1 ->
                    {
                        setWidth();
                        
                        yield "hstemhm";
                    }*/
                    /*
                        Hints
                     */
                    //Combining vstem and vstemhm into one call for the time being.
                    case 3, 23 ->
                    {
                        setWidthStems();
                        putHints(Hint.HintType.VERTICAL);
                        stack.clear();
                        yield "vstem";
                    }
                    //Combining hstem and hstemhm into one call for the time being.
                    case 1, 18 ->
                    {
                        setWidthStems();
                        putHints(Hint.HintType.HORIZONTAL);
                        stack.clear();
                        yield "hstem";
                    }
                    /*case 23 ->
                    {
                        setWidth();
                        
                        yield "vstemhm";
                    }*/
                    case 19 ->
                    {
                        setWidthStems();
    
                        System.out.println(stack);
                        
                        putHints(Hint.HintType.VERTICAL);
                        
                        yield "hintmask";
                    }
                    case 20 ->
                    {
                        setWidthStems();
    
                        System.out.println(stack);
    
                        putHints(Hint.HintType.VERTICAL);
    
                        yield "cntrmask";
                    }
                    case 21 ->
                    {
                        if(stack.size() % 2 == 1)
                        {
                            width = popBottom().intValue() + nominalWidth;
                            hasWidth = true;
                        }
                        point.add(popBottom().intValue(), popBottom().intValue());
                        //stack.clear();
                        yield "rmoveto";
                    }
                    case 4 ->
                    {
                        if(stack.size() > 1 && !hasWidth)
                        {
                            width = popBottom().intValue() + nominalWidth;
                            hasWidth = true;
                        }
                        
                        point.addY(popBottom().intValue());
                        //stack.clear();
                        yield "vmoveto";
                    }
                    case 22 ->
                    {
                        if(stack.size() > 1 && !hasWidth)
                        {
                            width = popBottom().intValue() + nominalWidth;
                            hasWidth = true;
                        }
                        
                        point.addX(popBottom().intValue());
                        //stack.clear();
                        yield "hmoveto";
                    }
                    case 5 ->
                    {
                        rLineTo();
                        //stack.clear();
                        yield "rlineto";
                    }
                    case 6 ->
                    {
                        hLineTo();
                        //stack.clear();
                        yield "hlineto";
                    }
                    case 7 ->
                    {
                        vLineTo();
                        //stack.clear();
                        yield "vlineto";
                    }
                    //Return operator does nothing but signify the end of a subroutine.
                    case 11 -> "return";
                    case 14 ->
                    {
                        //endchar - this character is finished.
                        //it's possible this is the only operator in a glyph which means that it's something akin to a space character and can be preceded by a width or use the default font width.
    
                        if(totalValues == 1)
                        {
                            System.out.println("Stack pre Pop: " + stack);
                            //This is a width overwrite.
                            width = (int)stack.pop() + (int) fontData.cffPrivateDict.getValue("nominalWidthX").get(0);
                        }
                        else if(totalValues == 0)
                        {
                            System.out.println("Stack without needing pop: " + stack);
                            //this uses the font default width.
                            width = (int) fontData.cffPrivateDict.getValue("defaultWidthX").get(0);
                        }
                        points.add(new StraightPoint(point).setWidth(width).setHintMask(hintMask));
    
                        //stack.clear();
                        yield "endchar";
                    }
                    case 24 ->
                    {
                        rCurveLine();
                        //stack.clear();
                        yield "rcurveline";
                    }
                    case 25 ->
                    {
                        rLineCurve();
                        //stack.clear();
                        yield "rlinecurve";
                    }
    
                    case 8 ->
                    {
                        rrCurveTo();
                        //stack.empty();
                        yield "rrcurveto";
                    }
                    case 26 ->
                    {
                        vvCurveTo();
                        //stack.empty();
                        yield "vvcurveto";
                    }
                    case 27 ->
                    {
                        hhCurveTo();
                        //stack.empty();
                        yield "hhcurveto";
                    }
                    case 29 ->
                    {
                        throw new RuntimeException("Global Subroutines not implemented.");
                        //yield "callgsubr";
                    }
                    case 30 ->
                    {
                        vhCurveTo();
                        //stack.clear();
                        yield "vhcurveto";
                    }
                    case 31 ->
                    {
                        hvCurveTo();
                        //stack.clear();
                        yield "hvcurveto";
                    }
                    case 12 ->
                    {
                        yield switch(operator2)
                                {
                                    case 0 -> throw new RuntimeException("This operators is deprecated apparently: " + operator2);
                                    case 1, 2, 6, 7, 8, 13, 16, 17, 19, 25, 31, 32, 33 -> throw new RuntimeException("Reserved 2-byte CharString operator: " + operator2);
                                    case 3 -> "and";
                                    case 4 -> "or";
                                    case 5 -> "not";
                                    case 9 -> "abs";
                                    case 10 -> "add";
                                    case 11 -> "sub";
                                    case 12 -> "div";
                                    case 14 -> "neg";
                                    case 15 -> "eq";
                                    case 18 -> "drop";
                                    case 20 -> "put";
                                    case 21 -> "get";
                                    case 22 -> "ifelse";
                                    case 23 -> "random";
                                    case 24 -> "mul";
                                    case 26 -> "sqrt";
                                    case 27 -> "dup";
                                    case 28 -> "exch";
                                    case 29 -> "index";
                                    case 30 -> "roll";
                                    case 34 -> "hflex";
                                    case 35 -> "flex";
                                    case 36 -> "hflex1";
                                    case 37 -> "flex1";
                                    default ->
                                    {
                                        throw new RuntimeException("Anything this high is reserved: " + operator2);
                                    }
                                };
                    }
                    default -> throw new RuntimeException("I don't think anything should be here...: " + operator1);
                };
        System.out.println("Operator Pushed: " + operatorStr + "(" + operator1 + ")");
        previousOperator = (!operatorStr.equals("return")) ? operatorStr : previousOperator;
        if(operatorStr.equals("endchar")) return;
    }
    
    
    
    
    private void vhCurveTo()
    {
        while(stack.size() > 0)
        {
            int c1x = point.x();
            int c1y = point.y() + popBottom().intValue();
            int c2x = c1x + popBottom().intValue();
            int c2y = c1y + popBottom().intValue();
            point.x(c2x + popBottom().intValue());
            point.y(c2y + (stack.size() == 1 ? popBottom().intValue() : 0));
            
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        
            if(stack.size() == 0)
            {
                break;
            }
        
            c1x = point.x() + popBottom().intValue();
            c1y = point.y();
            c2x = c1x + popBottom().intValue();
            c2y = c1y + popBottom().intValue();
            point.y(c2y + popBottom().intValue());
            point.x(c2x + (stack.size() == 1 ? popBottom().intValue() : 0));
            
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        }
        
        
/*        int size = stack.size();
        int mod = size % 8;
        
        if(mod == 0 || mod == 1)
        {
            size--;
            int x = 0;
            
            for(int i = 0; i < size; i += 8)
            {
                curveTo(0, popBottom().intValue(),
                        popBottom().intValue(), popBottom().intValue(),
                        popBottom().intValue(), 0);
                if(i + 8 == size)
                {
                    x = popAt(4).intValue();
                }
                curveTo(popBottom().intValue(), 0,
                        popBottom().intValue(), popBottom().intValue(),
                        x, popBottom().intValue());
            }
        }
        else
        {
            size--;
            int y = 0;
            
            if(size == 4)
            {
                y = popAt(4).intValue();
            }
            curveTo(0, popBottom().intValue(),
                    popBottom().intValue(), popBottom().intValue(),
                    popBottom().intValue(), y);
            y = 0;
            for(int i = 4; i < size; i += 8)
            {
                curveTo(popBottom().intValue(), 0,
                        popBottom().intValue(), popBottom().intValue(),
                        0, popBottom().intValue());
                if(i + 1 == size)
                {
                    y = popAt(4).intValue();
                }
                curveTo(0, popBottom().intValue(),
                        popBottom().intValue(), popBottom().intValue(),
                        popBottom().intValue(), y);
            }
        }*/
    }
    private void hvCurveTo()
    {
        while(stack.size() > 0)
        {
            int c1x = point.x() + popBottom().intValue();
            int c1y =  point.y();
            int c2x = c1x + popBottom().intValue();
            int c2y = c1y + popBottom().intValue();
            point.y(c2y + popBottom().intValue());
            point.x(c2x + (stack.size() == 1 ? popBottom().intValue() : 0));
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
            
            if(stack.size() == 0)
            {
                break;
            }
            
            c1x = point.x();
            c1y = point.y() + popBottom().intValue();
            c2x = c1x + popBottom().intValue();
            c2y = c1y + popBottom().intValue();
            point.x(c2x + popBottom().intValue());
            point.y(c2y + (stack.size() == 1 ? popBottom().intValue() : 0));
            
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        }
        
        
        /*int size = stack.size();
        int mod = size % 8;
        
        size--;
        if(mod == 0 || mod == 1)
        {
            int y = 0;
            for(int i = 0; i < size; i += 8)
            {
                curveTo(popBottom().intValue(), 0,
                        popBottom().intValue(), popBottom().intValue(),
                        0, popBottom().intValue());
                if(i + 8 == size)
                {
                    y = popAt(4).intValue();
                }
                curveTo(0, popBottom().intValue(),
                        popBottom().intValue(), popBottom().intValue(),
                        popBottom().intValue(), y);
            }
        }
        else
        {
            int x = 0;
            if(size == 4)
            {
                x = popAt(4).intValue();
            }
            curveTo(popBottom().intValue(), 0,
                    popBottom().intValue(), popBottom().intValue(),
                    x, popBottom().intValue());
            x = 0;
            for(int i = 4; i < size; i += 8)
            {
                curveTo(0, popBottom().intValue(),
                        popBottom().intValue(), popBottom().intValue(),
                        popBottom().intValue(), 0);
                if(i + 8 == size)
                {
                    x = popAt(4).intValue();
                }
                curveTo(popBottom().intValue(), 0,
                        popBottom().intValue(), popBottom().intValue(),
                        x, popBottom().intValue());
            }
        }*/
    }
    
    private void rLineCurve()
    {
        while(stack.size() > 6)
        {
            lineTo(popBottom().intValue(), popBottom().intValue());
        }
    
        int c1x = point.x() + popBottom().intValue();
        int c1y = point.y() + popBottom().intValue();
        int c2x = c1x + popBottom().intValue();
        int c2y = c1y + popBottom().intValue();
        point.x(c2x + popBottom().intValue());
        point.y(c2y + popBottom().intValue());
        curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
    
    }
    private void rCurveLine()
    {
        //Should automatically round down to the nearest 6, which should leave 2 leftover for lineTo
        while(stack.size() > 2)
        {
            int c1x = point.x() + popBottom().intValue();
            int c1y = point.y() + popBottom().intValue();
            int c2x = c1x + popBottom().intValue();
            int c2y = c1y + popBottom().intValue();
            point.x(c2x + popBottom().intValue());
            point.y(c2y + popBottom().intValue());
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        }
        
        lineTo(popBottom().intValue(), popBottom().intValue());
    }
    
    private void rrCurveTo()
    {
        while(stack.size() > 0)
        {
            int c1x = point.x() + popBottom().intValue();
            int c1y = point.y() + popBottom().intValue();
            int c2x = c1x + popBottom().intValue();
            int c2y = c1y + popBottom().intValue();
            point.x(c2x + popBottom().intValue());
            point.y(c2y + popBottom().intValue());
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        }
    }
    
    //Starts and ends vertical, which means the y value stays the same.
    private void vvCurveTo()
    {
        if(stack.size() % 2 == 1)
        {
            point.addX(popBottom().intValue());
        }
        while(stack.size() > 0)
        {
            int c1x = point.x();
            int c1y = point.y() + popBottom().intValue();
            int c2x = c1x + popBottom().intValue();
            int c2y = c1y + popBottom().intValue();
            point.x(c2x);
            point.y(c2y + popBottom().intValue());
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        }
        
/*        //Odd values let you set the y value before starting the flat curve.
        if(stack.size() % 2 == 1)
        {
            int x = popBottom().intValue();
            for(int i = 0; i < stack.size() / 4; i++)
            {
                curveTo(x, popBottom().intValue(),
                        popBottom().intValue(), popBottom().intValue(),
                        0, popBottom().intValue());
                x = 0;
            }
        }
        else
        {
            for(int i = 0; i < stack.size() / 4; i++)
            {
                curveTo(0, popBottom().intValue(),
                        popBottom().intValue(), popBottom().intValue(),
                        0, popBottom().intValue());
            }
        }*/
    }
    
    //Starts and ends horizontal, which means the y value stays the same.
    private void hhCurveTo()
    {
        if(stack.size() % 2 == 1)
        {
            point.addY(popBottom().intValue());
        }
        while(stack.size() > 0)
        {
            int c1x = point.x() + popBottom().intValue();
            int c1y = point.y();
            int c2x = c1x + popBottom().intValue();
            int c2y = c1y + popBottom().intValue();
            point.x(c2x + popBottom().intValue());
            point.y(c2y);
            curveTo(c1x, c1y, c2x, c2y, point.x(), point.y());
        }
        
       /* //Odd values let you set the y value before starting the flat curve.
        if(stack.size() % 2 == 1)
        {
            int y = popBottom().intValue();
            for(int i = 0; i < stack.size() / 4; i++)
            {
                curveTo(popBottom().intValue(), y,
                        popBottom().intValue(), popBottom().intValue(),
                        popBottom().intValue(), 0);
                y = 0;
            }
        }
        else
        {
            for(int i = 0; i < stack.size() / 4; i++)
            {
                curveTo(popBottom().intValue(), 0,
                        popBottom().intValue(), popBottom().intValue(),
                        popBottom().intValue(), 0);
            }
        }*/
    }
    
    private void curveTo(int xa, int ya, int xb, int yb, int xc, int yc)
    {
        //All of these points are relative, so they must be added to the previous point to get the actual position.
        /*xa += point.x();
        ya += point.y();
        xb += xa;
        yb += ya;
        xc += xb;
        yc += yb;*/
        points.add(new CurvedPoint(xa, ya, xb, yb, xc, yc));
        point.set(xc, yc);
    }
    
    private void rLineTo()
    {
        
        //for(int i = 0; i < stack.size() / 2; i++)
        while(!stack.isEmpty())
        {
            lineTo((int) popBottom(), (int) popBottom());
        }
    }
    
    private void vLineTo()
    {
        while(stack.size() > 0)
        {
            lineTo(0, (int) popBottom());
            if(stack.size() == 0)
            {
                break;
            }
    
            lineTo((int) popBottom(), 0);
        }
    }
    private void hLineTo()
    {
        while(stack.size() > 0)
        {
            lineTo((int) popBottom(), 0);
            if(stack.size() == 0)
            {
                break;
            }
    
            lineTo(0, (int) popBottom());
        }
    }

    private void lineTo(int x, int y)
    {
        point.add(x, y);
        points.add(new StraightPoint(point));
    }
    
    public Number popStack()
    {
        return stack.pop();
    }

    
    private void putHints(Hint.HintType hintType)
    {
        int pos = 0;
        while(!stack.isEmpty())
        {
            int edge1 = (int) popBottom();
            int edge2 = (int) popBottom();
        
            pos += edge1;
            int lowerEdge = pos;
            pos += edge2;
            //Defines a bottom edge for a stem
            hints.add(new Hint(lowerEdge, pos, hintType));
        }
    }
    
    private void setWidthStems()
    {
        //Stems are always in batches of 2
        if(stack.size() % 2 == 1)
        {
            System.out.println("Popped it for a width");
            width = nominalWidth + (int) popBottom();
            hasWidth = true;
        }
        //Else we don't do anything because we've already set a width.
    }
    
    private Number popBottom()
    {
        //Number number = (int) stack.get(0);
        //stack.remove(0);
        return popAt(0);
    }
    
    private Number popAt(int index)
    {
        Number number = stack.get(index);
        stack.remove(index);
        return number;
    }
}
