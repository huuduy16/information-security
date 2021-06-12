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


p = int(input("Enter p (prime):  "))
q = int(input("Enter q (prime):  "))
print()

n=p*q
print("n =  ",n)

phiN = (p-1)*(q-1)
print("ETF(n) =  ",phiN,'\n')

e = phiN
while math.gcd(e,phiN) != 1:
    e = int(input("Enter public key e (until gcd(e,ETF(n)) == 1):  "))

d = modulo_inverse_euclid(e,phiN)

print()
print("public key  e =  ",e)
print("->  private key  d =  ",d,'\n')

if d == -1:
    exit

print("message = -1 if you want to stop\n")

while 1:
    msg = int(input("Enter msg (hashed to int, must be less than 'n') :  "))
    if msg == -1:
        print("exit")
        break
    encoded = cal_pow(msg,e,n)
    print("Encoded code =  msg^e =  ",encoded)

    decoded = cal_pow(encoded,d,n)
    print("Decoded code = encoded^d = ",decoded)
    print()