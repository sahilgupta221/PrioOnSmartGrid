/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

/** 
 * Random waypoint movement where the coordinates are restricted to circular
 * area defined by a central point and range.
 * @author teemuk
 */
package movement;

import core.Coord;
import core.Settings;
import java.lang.*;

public class SensorMoteMobilityModel extends RandomWaypoint {
	/** Range of the cluster */
	public static final String	CLUSTER_RANGE = "clusterRange";
	/** Center point of the cluster */
        public static final String	CLUSTER_CENTER = "clusterCenter";
	public static final String	START_CLUSTER_CENTER = "startclusterCenter";
        public static final String	END_CLUSTER_CENTER = "endclusterCenter";
        /* number of guardians*/
        public static final String	GUAD_NUMBER = "guardiannumber";
	
	private double p_x_center = 100, p_y_center = 100;
        private double s_p_x_center = 100, s_p_y_center = 100;
        private double e_p_x_center = 100, e_p_y_center = 100;
	private double	p_range = 100.0;
        private double prevangle = 50;
        public static int formationIndex=-1;
        public int guardCount; /** how many nodes in this formation */
        private int nodeCount; /** how many nodes in this formation */
	static public int lastIndex=0; /** index of the previous node */
	private double angle = 50;
        
	public SensorMoteMobilityModel(Settings s) {
		super(s);
		
		if (s.contains(CLUSTER_RANGE)){
			this.p_range = s.getDouble(CLUSTER_RANGE);
		}
		if (s.contains(CLUSTER_CENTER)){
			double [] center = s.getCsvDoubles(CLUSTER_CENTER,2);
			this.p_x_center = center[0];
			this.p_y_center = center[1];
		}
                if (s.contains(START_CLUSTER_CENTER)){
			int[] s_center = s.getCsvInts(START_CLUSTER_CENTER,2);
			int[] e_center = s.getCsvInts(END_CLUSTER_CENTER,2);                        
			this.s_p_x_center = s_center[0];
			this.s_p_y_center = s_center[1];
                        this.e_p_x_center = e_center[0];
			this.e_p_y_center = e_center[1];
		}
                 angle = rng.nextDouble()*2 - 1;    
                 this.guardCount = s.getInt(GUAD_NUMBER);
                 this.nodeCount = s.getInt(core.SimScenario.NROF_HOSTS_S);
                 this.angle = 0;
	}
	
	private SensorMoteMobilityModel(SensorMoteMobilityModel cmv) {
		super(cmv);
		this.p_range = cmv.p_range;
		this.p_x_center = cmv.p_x_center;
		this.p_y_center = cmv.p_y_center;
                this.s_p_x_center = cmv.s_p_x_center;
	        this.s_p_y_center = cmv.s_p_y_center;
                this.e_p_x_center = cmv.e_p_x_center;
	        this.e_p_y_center = cmv.e_p_y_center;
                this.guardCount = cmv.guardCount;
                this.nodeCount = cmv.nodeCount;
                this.angle = 0;
                set_center_value_here();
	}
                
	@Override
	protected Coord randomCoord() {
            
                prevangle =angle;
                angle+=(6.28319/this.nodeCount)*this.lastIndex;
		double x = Math.sin(angle)*this.p_range;
		double y = Math.cos(angle)*this.p_range;
//		while (x*x + y*y>this.p_range*this.p_range) {
//			x = (rng.nextDouble()*2 - 1)*this.p_range;
//			y = (rng.nextDouble()*2 - 1)*this.p_range;
//		}
		x += this.p_x_center;
		y += this.p_y_center;

		return new Coord(x,y);
	}
        
	public void set_center_value_here()
        {
        double dx, dy;
	double placementFraction;
        int number_of_sensor_motes_per_guardian = this.nodeCount/this.guardCount;
	this.formationIndex = this.lastIndex++%number_of_sensor_motes_per_guardian==0?++this.formationIndex:this.formationIndex;
        // provide each sensor node center as per guardian number
        // formationIndex should be same for same guardian sensor node
        
	placementFraction = (1.0 * formationIndex / this.guardCount);
        
	dx = placementFraction * 
			(e_p_x_center - s_p_x_center);
	dy = placementFraction * 
			(e_p_y_center-s_p_y_center);
        p_x_center = s_p_x_center+dx;
        p_y_center = s_p_y_center+dy;
        
        }
        
	@Override
	public int getMaxX() {
		return (int)Math.ceil(this.p_x_center + this.p_range);
	}

	@Override
	public int getMaxY() {
		return (int)Math.ceil(this.p_y_center + this.p_range);
	}
	
	@Override
	public SensorMoteMobilityModel replicate() {
		return new SensorMoteMobilityModel(this);
	}
}
