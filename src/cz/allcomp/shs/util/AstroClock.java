package cz.allcomp.shs.util;

public class AstroClock {

	private double northLatitudeDegrees;
	private double northLatitudeMinutes;
	private double northLatitudeSeconds;
	private double easternLengthDegrees;
	private double easternLengthMinutes;
	private double easternLengthSeconds;
	private int zone;
	private Time time;
	
	public AstroClock(double northLatitudeDegrees, double northLatitudeMinutes, double northLatitudeSeconds,
			double easternLengthDegrees, double easternLengthMinutes, double easternLengthSeconds, int zone, Time time) {
		super();
		this.northLatitudeDegrees = northLatitudeDegrees;
		this.northLatitudeMinutes = northLatitudeMinutes;
		this.northLatitudeSeconds = northLatitudeSeconds;
		this.easternLengthDegrees = easternLengthDegrees;
		this.easternLengthMinutes = easternLengthMinutes;
		this.easternLengthSeconds = easternLengthSeconds;
		this.zone = zone;
		this.time = time;
	}

	private int sgn(double val) { 
		if (val == 0)
			return 0; 
		else if(val > 0) 
			return 1; 
		else
			return -1; 
	}
	
	public static void main(String[] args) {
		Time time = Time.getTime();
		AstroClock ac = new AstroClock(49.0, 45.0, 48.9, 14.0, 41.0, 4.0, 2, time);
		System.out.println(ac.compute());
	}
	
	public String compute() {
		String result = "";
		double p1 = Math.PI;
		double p2 = p1 * 2;
		double dr = p1 / 180.0;
		double k1 = 15 * dr * 1.0027379;
		double b5 = this.northLatitudeDegrees + ((1.0/60.0)*this.northLatitudeMinutes) + ((1.0/3600.0)*this.northLatitudeSeconds);
		double l5 = this.easternLengthDegrees + ((1.0/60.0)*this.easternLengthMinutes) + ((1.0/3600.0)*this.easternLengthSeconds);
		double h = -this.zone;
		l5 /= 360.0;
		double z0 = h/24.0;
		double y = this.time.getYear();
		double m = this.time.getMonth();
		double d = this.time.getDay();
		double g = 1;
		if(y < 1583)
			g = 0;
		double d1 = d;
		double f = d - d1 - 0.5;
		double j = -(int)(7.0 * (int)((m + 9.0) / 12.0) / 4.0);
		double j3;
		if(g != 0) {
			double s = sgn(m-9);
			double a = Math.abs(m-9);
			j3 = (int)(y+s*(int)(a/7.0));
			j3 = -(int)(((int)(j3 / 100.0) + 1) * 3.0 / 4.0);
		} else
			j3 = 0;
		j += (int)(275 * m / 9.0) + d1 + g*j3;
		j += 1721027 + 2*g + 367*y;
		if(f < 0) {
			f += 1;
			j -= 1;
		}
		double t = j - 2451545 + f;
		double tt = t / 36525.0 + 1;
		double t0 = t / 36525.0;
		double s = 24110.5 + 8640184.812999999 * t0;
		s += 86636.6 * z0 + 86400.0 * l5; 
		s /= 86400.0;
		s -= (int)s;
		t0 = s * 360.0 * dr;
		t += z0;
		double l = 0.779072 + 0.00273790931 * t;
		g = 0.993126 + 0.0027377785 * t;
		l -= (int)l;
		g -= (int)g;
		l *= p2;
		g *= p2;
		double v = 0.39785 * Math.sin(l); 
		v -= 0.01 * Math.sin(l - g);
		v += 0.00333 * Math.sin(l + g);
		v -= 0.00021 * tt * Math.sin(l); 
		double u = 1 - 0.03349 * Math.cos(g);
		u -= 0.00014 * Math.cos(2 * l);
		u += 0.00008 * Math.cos(l);
		double w = -0.0001 - 0.04129 *Math.sin(2 * l); 
		w += 0.03211 * Math.sin(g);
		w += 0.00104 * Math.sin(2 * l - g);
		w -= 0.00035 * Math.sin(2 * l + g);
		w -= 0.00008 * tt * Math.sin(g);
		s = w / Math.sqrt(u - v*v);
		double a5 = l + Math.atan(s / Math.sqrt(1 - s*s));
		s = v / Math.sqrt(u);
		double d5 = Math.atan(s / Math.sqrt(1- s*s));
		//double r5 = 1.00021 * Math.sqrt(u);
		double a_1 = a5;
		double d_1 = d5;
		t += 1;
		l = 0.779072 + 0.00273790931 * t;
		g = 0.993126 + 0.0027377785 * t;
		l -= (int)l;
		g -= (int)g;
		v = 0.39785 * Math.sin(l);
		v -= 0.01 * Math.sin(l - g);
		v += 0.00333 * Math.sin(l + g);
		v -= 0.00021 * tt * Math.sin(l);
		u = 1 - 0.03349 * Math.cos(g);
		u -= 0.00014 * Math.cos(2 * l);
		u += 0.00008 * Math.cos(l);
		w = -0.0001 - 0.04129 * Math.sin(2 * l);
		w += 0.03211 * Math.sin(g);
		w += 0.00104 * Math.sin(2 * l - g);
		w -= 0.00035 * Math.sin(2 * l + g);
		w -= 0.00008 * tt * Math.sin(g);
		s = w / Math.sqrt(u - v*v);
		a5 = l + Math.atan(s / Math.sqrt(1 - s*s));
		s = v / Math.sqrt(u);
		d5 = Math.atan(s / Math.sqrt(1 - s*s));
		//r5 = 1.00021 * Math.sqrt(u);
		double a_2 = a5;
		double d_2 = d5;
		if(a_2 < a_1)
			a_2 += p2;
		double z1 = dr * 90.833;
		s = Math.sin(b5 * dr);
		double c = Math.cos(b5 * dr);
		double z = Math.cos(z1);
		double m8 = 0;
		double w8 = 0;
		double a0 = a_1;
		double d0 = d_1;
		double da = a_2 - a_1;
		double dd = d_2 - d_1;
		double v2 = 0;
		for(double c0 = 0; c0 <= 23; c0++) {
			double p = (c0 + 1.0) / 24.0;
			double a2 = a_1 + p * da;
			double d2 = d_1 + p * dd;
			double l0 = t0 + c0 * k1;
			double l2 = l0 + k1;
			double h0 = l0 - a0;
			double h2 = l2 - a2;
			double h1 = (h2 + h0) / 2.0;
			d1 = (d2 + d0) / 2.0; 
			double v0 = 0;
			if(c0 <= 0)
				v0 = s * Math.sin(d0) + c * Math.cos(d0) * Math.cos(h0) - z;
			v2 = s * Math.sin(d2) + c * Math.cos(d2) * Math.cos(h2) - z;
			if(sgn(v0) != sgn(v2)) {
				double v1 = s * Math.sin(d1) + c * Math.cos(d1) * Math.cos(h1) - z;
				double a = 2 * v2 - 4 * v1 + 2 * v0;
				double b = 4 * v1 - 3 * v0 - v2;
				d = b * b - 4 * a * v0;
				if(d >= 0) {
					d = Math.sqrt(d);
					if((v0 < 0) && (v2 > 0))
						result += "sunrise:";
					if((v0 < 0) && (v2 > 0))
						m8 = 1;
					if((v0 > 0) && (v2 < 0))
						result += "sunset:";
					if((v0 > 0) && (v2 < 0))
						w8 = 1;
					double e = (-b + d) / (2.0 * a); 
					if((e > 1) || (e < 0))
						e = (-b - d) / (2 * a);
					double t3 = c0 + e + 1 / 120.0;
					double h3 = (int)t3;
					double m3 = (int)((t3 - h3) * 60.0); 
					String result_time = ""+m3;
					if(result_time.length() == 1) {
						result_time = "+" + result_time;
						result_time = h3 + ":" + result_time;
						result += result_time + ",";
						double h7 = h0 + e * (h2 - h0);
						double n7 = -Math.cos(d1) * Math.sin(h7);
						double d7 = c * Math.sin(d1) - s * Math.cos(d1) * Math.cos(h7);
						double az = Math.atan(n7 / d7) / dr;
						if(d7 < 0)
							az += 180;
						if(az < 0)
							az += 360;
						if(az > 360)
							az -= 360;
						String result_azimut = (int)az + " degree";
						result += result_azimut+ ";";
					}
				}
			}
			a0 = a2;
			d0 = d2;
			v0 = v2;
		}
		if((m8 == 0) && (w8 == 0)) {
			if(v2 < 0)
				result += "polarnight;";
			if(v2 > 0)
				result += "polarday;";
		} else {
			if(m8 == 0)
				result += "nosunrise;";
			if(w8 == 0)
				result += "nosunset;";
		}
		return result;
	}
}
