package ping;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * 服务器端所进行操作
 * @author ping
 *
 */
public class Server {

	public int[][] VB;
	public int[][] VB2;
	public int[][] VBP;
	public int[][] VBP2;
	public int[][] VBD;
	public int[][] VBD2;
	public int[] VR;
	public int[] VP;
	public int[] Length;
	int length;

	/**
	 * 根据数据集初始化
	 * @param _VB
	 */
	public Server(int[][] _VB) {
//		this._VB = _VB;
		VB = new int[_VB.length][];
		VB2 = new int[_VB.length][];
		for (int j = 0; j < _VB.length; j++) {
			int[] _VAA = new int[_VB[j].length * 2];
			int[] _VAA2 = new int[_VB[j].length * 2];
			for (int i = 0; i < _VB[j].length; i++) {
				_VAA[i] = 2 * _VB[j][i] + 1;
				_VAA2[i] = 2 * _VB[j][i];
			}

			int MODK = 1100;
			for (int i = _VB[j].length; i < _VB[j].length * 2; i++) {
				_VAA[i] = MODK - _VAA[i - _VB[j].length];
				_VAA2[i] = MODK - _VAA2[i - _VB[j].length];
			}
			VB2[j] = _VAA;
			VB[j] = _VAA2;
		}

		Random rd2 = new Random();
		length = VB[0].length;
		VR = new int[length];

		for (int i = 0; i < length; i++) {
			VR[i] = rd2.nextInt(25600);
		}

		VP = NRandom.getSequence(length);

		VBP = new int[VB.length][length];
		VBD = new int[VB.length][length];
		
		VBP2 = new int[VB.length][length];
		VBD2 = new int[VB.length][length];

		
		for (int i = 0; i < VB.length; i++) {
			for (int j = 0; j < length; j++) {
				VBP[i][j] = VB[i][j] + VR[j];
				VBP2[i][j] = VB2[i][j] + VR[j];
			}
		}

		for (int i = 0; i < VB.length; i++) {
			for (int j = 0; j < length; j++) {
				VBD[i][j] = VBP[i][VP[j]];
				VBD2[i][j] = VBP2[i][VP[j]];
			}
		}

	}
	/*
	 * public void changeVB(){ VB = new int[_VB.length][]; for(int j = 0; j <
	 * _VB.length; j++) { int[] _VAA = new int[_VB[j].length * 2]; for(int i =
	 * 0; i < _VB[j].length; i++) { _VAA[i] = 2 * _VB[j][i]; }
	 * 
	 * int MODK = 1100; for(int i = _VB[j].length; i < _VB[j].length * 2; i++) {
	 * _VAA[i] = MODK - _VAA[i - _VB[j].length]; } VB[j] = _VAA; }
	 * 
	 * VBP = new int[VB.length][length]; VBD = new int[VB.length][length];
	 * 
	 * for(int i = 0; i < VB.length; i++) { for(int j = 0; j < length; j++) {
	 * VBP[i][j] = VB[i][j] + VR[j]; } }
	 * 
	 * for(int i = 0; i < VB.length; i++) { for(int j = 0; j < length; j++) {
	 * VBD[i][j] = VBP[i][VP[j]] ; } } }
	 */

	
	/**
	 * 服务器执行的FSPP第二部分
	 * @param alpha
	 * @param CC
	 * @return
	 */
	public BigInteger[] ServerFirstStep(BigInteger alpha, BigInteger[] CC) {
		BigInteger[] CC1 = Arrays.copyOfRange(CC, 0, CC.length/2);
		BigInteger[] CC2 = Arrays.copyOfRange(CC, CC.length/2, CC.length);
 		BigInteger[] vdd1 = FSPP_CSP_Second_Step.NewStepTwo(VR, VP, alpha, CC1);
 		BigInteger[] vdd2 = FSPP_CSP_Second_Step.NewStepTwo(VR, VP, alpha, CC2);
		BigInteger[] vdd = new BigInteger[vdd1.length * 2];
		
		for(int i = 0; i < vdd1.length; i++)
		{
			vdd[i] = vdd1[i];
			vdd[i + vdd1.length] = vdd2[i];
		}
 		return vdd;
	}

	/**
	 * 服务器执行的FSCIP第二步
	 * @param CC
	 * @param lengthK
	 * @return
	 */
	public BigInteger[][][] ServerSecondStep(BigInteger[][][] CC, int[] lengthK) {
		int[] lengthK1 = Arrays.copyOfRange(lengthK, 0, lengthK.length/2);
		int[] lengthK2 = Arrays.copyOfRange(lengthK, lengthK.length/2, lengthK.length);
		
		BigInteger[][][] CC1 = Arrays.copyOfRange(CC, 0, CC.length/2);
		BigInteger[][][] CC2 = Arrays.copyOfRange(CC, CC.length/2, CC.length);
		
		BigInteger[][][] result = new BigInteger[VB.length * 2][][];
		BigInteger[][][] result1 = new BigInteger[VB.length][length][];
		BigInteger[][][] result2 = new BigInteger[VB.length][length][];
		
		for (int i = 0; i < VB.length; i++) {
			for (int j = 0; j < length; j++) {
				result1[i][j] = FSICP_CSP_Second_Step.NewStepTwo(VBD[i][j], lengthK1[j], CC1[j]);
				result2[i][j] = FSICP_CSP_Second_Step.NewStepTwo(VBD2[i][j], lengthK2[j], CC2[j]);
			}
			result[i] = result1[i];
			result[i + VB.length] = result2[i];
		}
		return result;
	}

	/**
	 * 服务器提供置换后的v向量
	 * @return
	 */
	public String getLttp() {
		String lttp;
		int CCC = length / 2;
		int[] VAK1 = new int[length];
		int[] VAK2 = new int[length];
		for (int i = 0; i < CCC; i++) {
			VAK1[i] = 1;
			VAK1[i + CCC] = 0;
		}
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < length; i++) {
			VAK2[i] = VAK1[VP[i]];
			temp.append(Integer.toString(VAK2[i]));
		}
		lttp = temp.toString();
		return lttp;
	}
}
