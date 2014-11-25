from random import randrange
def rep(m1,m2):
   p1 = 76
   p2 = 72
   p3 = 71
   p4 = 64
   p1non = 1 #Starting at Ball State game
   p2non = 0
   p3non = 0
   p4non = 0
   p1bcs = 1
   p2bcs = 0
   p3bcs = 0
   p4bcs = 0
   k = randrange(2)
   if k == p1non:
      p1 += m1
   if k == p2non:
      p2 += m1
   if k == p3non:
      p3 += m1
   if k == p4non:
      p4 += m1
   k2 = randrange(2)
   if k2 == p1bcs:
      p1 += m2
   if k2 == p2bcs:
      p2 += m2
   if k2 == p3bcs:
      p3 += m2
   if k2 == p4bcs:
      p4 += m2
   aList = [p1,p2,p3,p4]
   for x in range(len(aList)):
      if aList[x] == max(aList):
         return x

trials = 10000
print("After the Clemson game, and with {} trials:".format(trials))

bList = []
for x in range(trials):
   bList.append(rep(4,7))

num1 = 0
for x in range(len(bList)):
   if bList[x] == 0:
      num1 += 1
pct1 = num1/len(bList)
print("Aaron's chance of winning is: {:.2f}%".format(pct1*100))

num2 = 0
for x in range(len(bList)):
   if bList[x] == 1:
      num2 += 1
pct2 = num2/len(bList)
print("Ryan's chance of winning is: {:.2f}%".format(pct2*100))

num3 = 0
for x in range(len(bList)):
   if bList[x] == 2:
      num3 += 1
pct3 = num3/len(bList)
print("Kyle's chance of winning is: {:.2f}%".format(pct3*100))

num4 = 0
for x in range(len(bList)):
   if bList[x] == 3:
      num4 += 1
pct4 = num4/len(bList)
print("Max's chance of winning is: {:.2f}%".format(pct4*100))