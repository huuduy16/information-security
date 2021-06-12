//PhanHuuDuy_18020015
package ecelgamal;

import static ecelgamal.Utils.NUM3;
import static ecelgamal.Utils.POINT_O;
import static ecelgamal.Utils.cal_mul;
import static ecelgamal.Utils.module_inverse_euclid;
import static ecelgamal.Utils.pow2;
import static ecelgamal.Utils.pow3;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.Scanner;

public class ECElGamal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EllipticCurve ec = inputEC(scanner);
        System.out.println("Elliptic curve:  " + ec.toString() + '\n');

        Point basePoint = inputBasePoint(ec, scanner);
        System.out.println("Base point:  " + basePoint.toString() + '\n');

        BigInteger a = inputPrivateA(ec, basePoint, scanner);

        Point betaPoint = cal_mul(basePoint, a, ec);
        System.out
            .println("->  public key  Beta point = a * base_point =  " + betaPoint.toString());

        System.out.println();

        BigInteger k;
        Point msg;
        Point M1, M2, decoded;
        while (true) {
            k = inputRandomK(scanner);
            msg = inputMsg(ec, scanner);

            if (msg.getX().equals(Utils.NEGATIVE_ONE) || msg.getY().equals(Utils.NEGATIVE_ONE)) {
                System.out.println("EXIT");
                break;
            }

            System.out.println("M =  " + msg.toString());

            //M1 = k * base_point
            M1 = cal_mul(basePoint, k, ec);
            //M2 = M + k * beta_point
            M2 = msg.add(cal_mul(betaPoint, k, ec), ec);

            System.out.println("Encoded:  ");
            System.out.println("    M1 = k * base_point =  " + M1.toString());
            System.out.println("    M2 = M + k * beta_point =  " + M2.toString());

            //M = M2 - a*M1 = M2 + (-(a*M1))
            decoded = M2.add(cal_mul(M1, a, ec).toNegative(), ec);
            System.out.println("Decoded = M2 - a*M1 =  " + decoded.toString());

            if (msg.isEqual(decoded)) {
                System.out.println("->  TRUE:  msg == decoded\n");
            } else {
                System.out.println("->  FALSE:  msg != decoded");
                break;
            }
        }

        scanner.close();
    }

    public static Point inputMsg(EllipticCurve ec, Scanner scanner) {
        System.out.println("Enter message M (hashed to a point of EC, -1 to stop)");
        Point P = new Point();

        BigInteger x, y;
        do {
            System.out.print("Enter x:  ");
            x = scanner.nextBigInteger();
            System.out.print("Enter y:  ");
            y = scanner.nextBigInteger();
            P.setX(x);
            P.setY(y);
            if (x.equals(Utils.NEGATIVE_ONE) || y.equals(Utils.NEGATIVE_ONE)) {
                break;
            }
        } while (!ec.contains(P));

        return P;
    }

    public static BigInteger inputRandomK(Scanner scanner) {
        BigInteger k;
        System.out.print("Enter random k :  ");
        k = scanner.nextBigInteger();
        return k;
    }


    public static BigInteger inputPrivateA(EllipticCurve ec, Point point, Scanner scanner) {
        BigInteger a;
        do {
            System.out.print("Enter private key a (must be less than '#E') :  ");
            a = scanner.nextBigInteger();

        } while (cal_mul(point, a, ec).isEqual(POINT_O));
        return a;
    }

    public static EllipticCurve inputEC(Scanner scanner) {
        System.out.println("Enter Elliptic curve");
        EllipticCurve ec = new EllipticCurve();

        BigInteger n, a, b;
        System.out.print("Enter n (prime):  ");
        n = scanner.nextBigInteger();
        ec.setMod(n);

        do {
            System.out.print("Enter a:  ");
            a = scanner.nextBigInteger();
            System.out.print("Enter b:  ");
            b = scanner.nextBigInteger();
            ec.setA(a);
            ec.setB(b);
        } while (!ec.isValidEC());

        return ec;
    }

    public static Point inputBasePoint(EllipticCurve ec, Scanner scanner) {
        System.out.println("Enter base point P of EC");
        Point P = new Point();

        BigInteger x, y;
        do {
            System.out.print("Enter x:  ");
            x = scanner.nextBigInteger();
            System.out.print("Enter y:  ");
            y = scanner.nextBigInteger();
            P.setX(x);
            P.setY(y);
        } while (!ec.contains(P));

        return P;
    }
}

class Utils {

    static final BigInteger NEGATIVE_ONE = new BigInteger("-1");
    static final BigInteger NUM3 = new BigInteger("3");
    static final BigInteger NUM4 = new BigInteger("4");
    static final BigInteger NUM23 = new BigInteger("23");
    static final Point POINT_O = new Point(NEGATIVE_ONE, NEGATIVE_ONE);

    //cal  (a^3) % mod_
    public static BigInteger pow3(BigInteger a, BigInteger mod_) {
        return new BigInteger(a.multiply(a).multiply(a).remainder(mod_).toString());
    }

    //cal  (a^2) % mod_
    public static BigInteger pow2(BigInteger a, BigInteger mod_) {
        return new BigInteger(a.multiply(a).remainder(mod_).toString());
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

    //cal k*point in Elliptic curve ec
    public static Point cal_mul(Point point, BigInteger k, EllipticCurve ec) {
        if (k.equals(BigInteger.ONE)) {
            return point;
        }
        BigInteger[] tmp = k.divideAndRemainder(TWO);
        Point rs = cal_mul(point, tmp[0], ec);
        if (rs.isEqual(POINT_O)) {
            return rs;
        }

        rs = rs.add(rs, ec);
        if (rs.isEqual(POINT_O)) {
            return rs;
        }

        if (tmp[1].equals(BigInteger.ONE)) {
            rs = rs.add(point, ec);
        }
        return rs;
    }
}

class EllipticCurve {

    BigInteger a;
    BigInteger b;
    BigInteger mod;

    public EllipticCurve() {
    }

    public String toString() {
        return "y^2 = x^3 + " + a + "*x + " + b + "    (mod  " + mod + ")";
    }

    //check whether Elliptic curve contains (x,y)
    public boolean contains(Point point) {
        //tmp1 = y^2
        BigInteger tmp1 = new BigInteger(pow2(point.getY(), mod).toString());
        //zz = ax + b
        BigInteger zz = new BigInteger(
            ((a.multiply(point.getX())).add(b)).remainder(mod).toString());
        //tmp2 = x^3 + ax + b
        BigInteger tmp2 = new BigInteger(
            (pow3(point.getX(), mod).add(zz)).remainder(mod).toString());
        return tmp1.equals(tmp2);
    }

    //check whether a and b can make an Elliptic curve
    public boolean isValidEC() {
        BigInteger tmp1 = new BigInteger(
            Utils.NUM4.multiply(pow3(a, mod)).remainder(mod).toString());
        BigInteger tmp2 = new BigInteger(Utils.NUM23.multiply(pow2(b, mod)).toString());
        BigInteger rs = new BigInteger(tmp1.add(tmp2).toString());
        return !rs.equals(BigInteger.ZERO);
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getMod() {
        return mod;
    }

    public void setMod(BigInteger mod) {
        this.mod = mod;
    }
}

class Point {

    BigInteger x;
    BigInteger y;

    public Point() {
    }

    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public boolean isEqual(Point point) {
        return (x.equals(point.getX())) && (y.equals(point.getY()));
    }

    public String toString() {
        return "( " + x + " , " + y + " )";
    }

    public Point toNegative() {
        return new Point(x, ZERO.subtract(y));
    }

    //cal P + Q = (x3,y3) in Elliptic curve ec
    public Point add(Point point, EllipticCurve ec) {
        BigInteger mod = ec.getMod();
        BigInteger a = ec.getA();
        BigInteger x1 = x, y1 = y;
        BigInteger x2 = point.getX(), y2 = point.getY();

        BigInteger tmp1, tmp2, lambda;
        if (this.isEqual(point)) {
            //tmp1 = 3 * (x1^2) + a
            tmp1 = new BigInteger((NUM3.multiply(pow2(x1, mod))).add(a).remainder(mod).toString());
            //tmp2 = (2 * y1)^(-1)
            tmp2 = new BigInteger(module_inverse_euclid(TWO.multiply(y1), mod).toString());

            //lambda = (3 * (x1^2) + a) * ((2 * y1)^(-1))
            lambda = new BigInteger(tmp1.multiply(tmp2).remainder(mod).toString());
        } else {
            if (x1.equals(x2)) {
                return POINT_O;
            }
            //tmp1 = y2 - y1
            tmp1 = new BigInteger(y2.subtract(y1).remainder(mod).toString());
            //tmp2 = (x2 - x1)^(-1)
            tmp2 = new BigInteger(module_inverse_euclid(x2.subtract(x1), mod).toString());

            //lambda = (y2 - y1) * ((x2 - x1)^(-1))
            lambda = new BigInteger(tmp1.multiply(tmp2).remainder(mod).toString());
        }

        Point rs = new Point();
        //x3 = lambda^2 - x1 - x2 = lambda^2 - (x1 + x2)
        tmp1 = x1.add(x2);
        tmp2 = pow2(lambda, mod).subtract(tmp1).remainder(mod);
        rs.setX(tmp2.add(mod).remainder(mod));

        //y3 = lambda*(x1-x3) - y1
        tmp1 = lambda.multiply(x1.subtract(rs.getX()));
        tmp2 = tmp1.subtract(y1).remainder(mod);
        rs.setY(tmp2.add(mod).remainder(mod));
        return rs;
    }
}