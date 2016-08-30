/*
 * Copyright 2014 Petr Mikše. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cz.allcomp.shs.allcomplib.transducers;

import cz.allcomp.shs.allcomplib.common.StatisticInt;

/**
 * implements org.omg.CORBA.portable.StreamableValue is needed to workaround
 * a java CORBA implementation error; unableLocateValueHelper exception is 
 * thrown otherwise
 *
 * @author petr
 */
class StIntImpl extends StInt implements org.omg.CORBA.portable.StreamableValue{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** an empty StatisticInt instance */
    public static final StIntImpl Zero = new StIntImpl(0, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    
    public StIntImpl() {
    }
    

    public StIntImpl(int count, int sum, long sum2, int max, int min) {
        //super(count, sum, sum2, max);
	super.count=count;
	super.sum=sum;
	super.sum2=sum2;
	super.max=max;
	super.min=min;
    }

    public StIntImpl(StatisticInt si) {
        //super(count, sum, sum2, max);
	super.count=si.count;
	super.sum=si.sum;
	super.sum2=si.sum2;
	super.max=si.max;
	super.min=si.min;
    }

    /**
     *
     * @param nextItem item to add to the series
     * @return an instance with next item counted
     */
    public StIntImpl addItem(int nextItem){
        return new StIntImpl (count+1, sum+nextItem, sum2+(long)nextItem^2, nextItem>max ? nextItem : max, nextItem<min ? nextItem : min);
    }

    /**
     *
     * @param series to add to this series
     * @return an instance with the two series added
     */
    public StIntImpl addSeries(StInt series){
        return new StIntImpl (this.count+series.count, this.sum+series.sum, this.sum2+series.sum2, series.max>this.max ? series.max : this.max, series.min<this.min ? series.min : this.min);
    }

    /**
     *
     * @return an average value from given series
     */
    @Override
    public int getAverage(){
        if (count<=0) return Integer.MIN_VALUE;
        return (sum+(count>>1))/count;
    }

    /**
     * Calculate average square deviation from the average value from given series
     *
     * @return an average square deviation
     */
    @Override
    public int getAverageSquareDeviation(){
        if (count<=0) return Integer.MIN_VALUE;
        long diff=sum2-(((long)sum)^2)/count;
        return (int)(diff/count);
    }

    /**
     *
     * @return a maximum from given series
     */
    @Override
    public int getMaximum(){
        return max;
    }

    /**
     *
     * @return number of items in this series
     */
    @Override
    public int getCount(){
        return count;
    }

    @Override
    public int getMinimum() {
	return min;
    }
}
