//PhanHuuDuy_18020015
package elgamal;

import static java.math.BigInteger.TWO;

import java.math.BigInteger;
import java.util.Scanner;

public class ElGamal {

    static final BigInteger NEGATIVE_ONE = new BigInteger("-1");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter p (prime):  ");
            BigInteger p = scanner.nextBigInteger();

            BigInteger alpha;

            do {
                System.out.print("Enter alpha (primitive root of p):  ");
                alpha = scanner.nextBigInteger();
            } while (!isPrimitiveRoot(alpha, p));
            System.out.println("alpha =  " + alpha + '\n');

            System.out.print("Enter private key a :  ");
            BigInteger a = scanner.nextBigInteger();

            BigInteger beta = new BigInteger(cal_pow(alpha, a, p).toString());
            System.out.println("->  public key  beta = alpha^a =  " + beta + '\n');

            BigInteger msg;
            BigInteger y1, y2, decoded;
            while (true) {
                System.out.print("Enter random k :  ");
                BigInteger k = scanner.nextBigInteger();

                System.out
                    .print("Enter msg (hashed to int, must be less than 'p', -1 to stop) :  ");
                msg = scanner.nextBigInteger();

                if (msg.equals(NEGATIVE_ONE)) {
                    System.out.println("EXIT");
                    break;
                }

                y1 = new BigInteger(cal_pow(alpha, k, p).toString());
                y2 = new BigInteger((msg.multiply(cal_pow(beta, k, p))).remainder(p).toString());

                System.out.println("Encoded:  ");
                System.out.println("    y1 = alpha^k =  " + y1);
                System.out.println("    y2 = msg * (beta^k) =  " + y2);

                decoded = new BigInteger(
                    y2.multiply(module_inverse_euclid(cal_pow(y1, a, p), p)).remainder(p)
                        .toString());
                System.out.println("Decoded = y2 * ((y1^a)^-1) =  " + decoded);

                if (msg.equals(decoded)) {
                    System.out.println("->  TRUE:  msg == decoded\n");
                } else {
                    System.out.println("->  FALSE:  msg != decoded");
                    break;
                }
            }
        }
    }

    //cal  (a^b) % mod_
    public static BigInteger cal_pow(BigInteger a, BigInteger b, BigInteger mod_) {
        if (BigInteger.ZERO.equals(b)) {
            return BigInteger.ONE.mod(mod_);
        }
        if (BigInteger.ONE.equals(b)) {
            return a.mod(mod_);
        }
        BigInteger[] tmp = b.divideAndRemainder(TWO);
        BigInteger rs = cal_pow(a, tmp[0], mod_);
        rs = rs.multiply(rs).mod(mod_);
        if (tmp[1].equals(BigInteger.ONE)) {
            rs = rs.multiply(a).mod(mod_);
        }
        return rs;
    }

    //extend Euclid algorithm
    public static BigInteger[] ext_euclid(BigInteger a, BigInteger b) {
        BigInteger m = new BigInteger(a.toString());
        BigInteger n = new BigInteger(b.toString());
        BigInteger xm = BigInteger.ONE, ym = BigInteger.ZERO;
        BigInteger xn = BigInteger.ZERO, yn = BigInteger.ONE;

        BigInteger[] tmp;
        BigInteger q, r, xr, yr;
        while (!n.equals(BigInteger.ZERO)) {
            tmp = m.divideAndRemainder(n);
            q = tmp[0];
            r = tmp[1];
            xr = xm.subtract(q.multiply(xn));
            yr = ym.subtract(q.multiply(yn));

            m = new BigInteger(n.toString());

            xm = new BigInteger(xn.toString());
            ym = new BigInteger(yn.toString());

            n = new BigInteger(r.toString());

            xn = new BigInteger(xr.toString());
            yn = new BigInteger(yr.toString());
        }
        BigInteger[] rs = new BigInteger[2];
        rs[0] = xm;
        rs[1] = ym;
        return rs;
    }

    //cal  x^(-1) % mod_
    public static BigInteger module_inverse_euclid(BigInteger x, BigInteger mod_) {
        x = x.remainder(mod_).add(mod_).remainder(mod_);
        BigInteger[] tmp = ext_euclid(x, mod_);
        BigInteger a = new BigInteger(tmp[0].toString());
        BigInteger b = new BigInteger(tmp[1].toString());

        BigInteger ax = new BigInteger(a.multiply(x).toString());
        BigInteger by = new BigInteger(b.multiply(mod_).toString());
        if (ax.add(by).equals(BigInteger.ONE)) {
            return a.remainder(mod_).add(mod_).remainder(mod_);
        }
        return NEGATIVE_ONE;
    }

    //check whether alpha is primitive of p
    public static boolean isPrimitiveRoot(BigInteger alpha, BigInteger p) {
        return cal_pow(alpha, p.subtract(BigInteger.ONE), p).equals(BigInteger.ONE);
    }
}