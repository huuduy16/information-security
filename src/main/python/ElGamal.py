#Phan Hữu Duy _ 18020015

import math

def cal_pow(a: int, b: int, mod: int):
    # return pow(a,b,mod)
    if b == 0:
        return 1%mod
    if b == 1:
        return a%mod
    rs=cal_pow(a,b//2,mod)
    rs=(rs*rs)%mod
    if b%2 == 1:
        rs=(rs*a)%mod
    return rs

def func_euclid(a: int, b: int):
    m, n = a, b
    xm, ym = 1, 0
    xn, yn = 0, 1
    while (n != 0):
        q = m // n # chia lấy phần nguyên
        r = m % n # chia lấy phần dư
        xr, yr = xm - q*xn, ym - q*yn
        m = n
        xm, ym = xn, yn
        n = r
        xn, yn = xr, yr
    return (xm, ym) # m = gcd(a,b) = xm * a + ym * b

def modulo_inverse_euclid(x: int, mod: int):
    (a,b) = func_euclid(x,mod)
    if a*x + b*mod != 1:
        return -1 #not exist
    return a%mod

def isPrimitiveRoot(alpha: int, p: int):
    if cal_pow(alpha,p-1,p) == 1:
        return True
    return False


p = int(input("Enter p (prime):  "))
print()

alpha=p-1
while 1:
    alpha = int(input("Enter alpha (primitive root of p):  "))
    if isPrimitiveRoot(alpha,p):
        break

print("alpha =  ",alpha,'\n')


a = int(input("Enter private key a :  "))

print()

beta = cal_pow(alpha,a,p)
print("->  public key  beta = alpha^a =  ",beta,'\n')

print("message = -1 if you want to stop\n")

while 1:
    k = int(input("Enter k (random):  "))

    msg = int(input("Enter msg (hashed to int, must be less than 'p') :  "))
    if msg == -1:
        print("exit")
        break

    y1 = cal_pow(alpha,k,p)
    y2 = (msg * cal_pow(beta,k,p)) % p

    print("Encoded:  ")
    print("    y1 = alpha^k =  ",y1)
    print("    y2 = msg * (beta^k) =  ",y2)

    decoded = (y2 * modulo_inverse_euclid(cal_pow(y1,a,p),p)) % p
    print("Decoded = y2 * ((y1^a)^-1) = ",decoded)
    print()