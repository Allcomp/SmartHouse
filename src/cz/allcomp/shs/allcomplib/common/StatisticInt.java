/*
 * Copyright 2010 Petr Mikše. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package cz.allcomp.shs.allcomplib.common;

/**
 * Class calculating maximal and average values and an average square deviation from given series
 *
 * @author Petr Mikše
 */
public final class StatisticInt {
    /** an empty StatisticInt instance */
    public static final StatisticInt Zero = new StatisticInt(0, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    /** number of the items */
    public final int count;
    /** sum of the items    */
    public final int sum;
    /** sum of the squared items    */
    public final long sum2;
    /** maximum item    */
    public final int max;
    /** minimum item    */
    public final int min;

    /**
     * return an instance with a single item counted
     * @param firstItem initial item
     */
    public StatisticInt(int firstItem){
        this(1, firstItem, (long)firstItem^2, firstItem, firstItem);
    }

    private StatisticInt(int count, int sum, long sum2, int max, int min){
        this.count=count;
        this.sum=sum;
        this.sum2=sum2;
        this.max=max;
        this.min=min;
    }

    /**
     *
     * @param nextItem item to add to the series
     * @return an instance with next item counted
     */
    public StatisticInt addItem(int nextItem){
        return new StatisticInt (count+1, sum+nextItem, sum2+(long)nextItem^2, nextItem>max ? nextItem : max, nextItem<min ? nextItem : min);
    }

    /**
     *
     * @param series to add to this series
     * @return an instance with the two series added
     */
    public StatisticInt addSeries(StatisticInt series){
        return new StatisticInt (this.count+series.count, this.sum+series.sum, this.sum2+series.sum2, series.max>this.max ? series.max : this.max, series.min>this.min ? series.min : this.min);
    }

    /**
     *
     * @return an average value from given series
     */
    public int getAverage(){
        if (count<=0) return Integer.MIN_VALUE;
        return (sum+(count>>1))/count;
    }

    /**
     * Calculate average square deviation from the average value from given series
     *
     * @return an average square deviation
     */
    public int getAverageSquareDeviation(){
        if (count<=0) return Integer.MIN_VALUE;
        long diff=sum2-(((long)sum)^2)/count;
        return (int)(diff/count);
    }

    /**
     *
     * @return a maximum from given series
     */
    public int getMaximum(){
        return max;
    }

     /**
     *
     * @return a minimum from given series
     */
    public int getMinimum(){
        return min;
    }

   /**
     *
     * @return number of items in this series
     */
    public int getCount(){
        return count;
    }
}
