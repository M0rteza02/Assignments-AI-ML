public class StateAndReward {
	private static final double minAngel = -Math.PI;
	private static final double maxAngel = Math.PI; 
	private static final Integer states = 4;
	

	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {
		int dAngle = discretize2(angle, 100, minAngel, maxAngel);
		String state = Integer.toString(dAngle);
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
	
		double reward = Math.PI - Math.abs(angle);
		return reward;
	//	return Math.pow((1 - Math.abs(angle)/maxAngel), 2);
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */

		String a = getStateAngle(angle, vx, vy);
		String Ang = Integer.toString(discretize2(angle, 4, minAngel, maxAngel));
		String VelX = Integer.toString(discretize(vx, 4, -2, 2));
		String VelY = Integer.toString(discretize(vy, 4, -2, 2));
		String state = Ang + "|" + VelX + "|" + VelY;
		//System.out.println(state);
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		// String state = getStateHover(angle, vx, vy);
		double reward = 0;
		// //double a = state.charAt(0);
		// int x = Character.getNumericValue(state.charAt(2));
		// int y = Character.getNumericValue(state.charAt(4));
		double angleReward = getRewardAngle(angle, vx, vy); 
		double vxReward = 2 - Math.abs(vx);
		double vyReward = 2 - Math.abs(vy);
		
		reward = angleReward + vxReward + vyReward;
		
		
		//System.out.println(angleReward);


		// double vxReward = Math.abs(x/5); 
		// double vyReward = Math.abs(y/5); 
		//System.out.println(reward);
		// switch(x){
		// 	case 0:
		// 		reward += 1;
		// 		break;
		// 	case 1:
		// 		reward += 1;
		// 		break;
		// 	case 2:
		// 		reward += 2;
		// 		break;
		// 	case 3:
		// 		reward += 8;
		// 		break;
		// 	case 4:
		// 		reward += 2;
		// 		break;
		// 	case 5:
		// 		reward += 1;
		// 		break;
		// 	case 6:
		// 		reward += 1;
		// 		break;
		// }
		// switch(y){
		// 	case 0:
		// 		reward += 1;
		// 		break;
		// 	case 1:
		// 		reward += 1;
		// 		break;
		// 	case 2:
		// 		reward += 2;
		// 		break;
		// 	case 3:
		// 		reward += 16;
		// 		break;
		// 	case 4:
		// 		reward += 2;
		// 		break;
		// 	case 5:
		// 		reward += 1;
		// 		break;
		// 	case 6:
		// 		reward += 1;
		// 		break;
		// }

	
		return reward;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
