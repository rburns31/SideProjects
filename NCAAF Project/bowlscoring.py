from random import randrange
from Graphics import *
def rep(m1,m2):
   p1 = 0
   p2 = 0
   p3 = 0
   p4 = 0
   p1non = [0,0,0,0,0,1,0,0,0,0,1,0,1,1,0,0,1,0,1]
   p2non = [1,0,1,0,1,0,0,1,0,0,1,0,0,1,0,1,0,1,0]
   p3non = [1,1,1,1,0,0,1,0,0,1,0,1,1,0,0,0,0,0,0]
   p4non = [1,0,1,0,0,0,0,1,1,1,0,0,1,0,1,1,0,0,0]
   p1bcs = [0,1]
   p2bcs = [0,0]
   p3bcs = [1,0]
   p4bcs = [0,0]
   for x in range(len(p1non)):
      k = randrange(2)
      if k == p1non[x]:
         p1 += m1
      if k == p2non[x]:
         p2 += m1
      if k == p3non[x]:
         p3 += m1
      if k == p4non[x]:
         p4 += m1
   for x in range(len(p1bcs)):
      k = randrange(2)
      if k == p1non[x]:
         p1 += m2
      if k == p2non[x]:
         p2 += m2
      if k == p3non[x]:
         p3 += m2
      if k == p4non[x]:
         p4 += m2
   aList = [p1,p2,p3,p4]
   aList.sort()
   diff = aList[3] - aList[0]
   return(diff)

trials = 10000

bList = []
for x in range(trials):
   bList.append(rep(4,7))
   bList.sort()
print("The maximum differential with {} trials is:".format(trials), max(bList))
avg = sum(bList)/len(bList)
print("The average differential with {} trials is:".format(trials), avg)

cList = []
for x in range(len(bList)):
   test = bList[x] in cList
   if test == False:
      cList.append(bList[x])

occur = []
for item in cList:
   num = 0
   for x in range(len(bList)):
      if bList[x] == item:
         num += 1
   occur.append(num)

## for x in range(len(occur)):
##    if occur[x] == max(occur):
##       maxval = cList[x]
## 
## w = 400
## h = 500
## hmult = 400/max(occur)
##
## plot = Window("Differentials",w,h)
## for x in range(len(cList)):
##    if (hmult*(max(occur)-occur[x]-.03)) < 50:
##       bar = Line((50+(cList[x]*6),h-50),(50+(cList[x]*6),50))
##       bar.draw(plot)
##    else:
##       bar = Line((50+(cList[x]*6),h-50),(50+(cList[x]*6),hmult*(max(occur)-occur[x]-.03)))
##       bar.draw(plot)
## 
## words = Text((50+maxval*6,h-25),str(maxval))
## words.fill = Color("black")
## words.rotate(90)
## words.draw(plot)
##
## words = Text((50+cList[0]*6,h-25),str(cList[0]))
## words.fill = Color("black")
## words.rotate(90)
## words.draw(plot)
##
## words = Text((50+cList[::-1][0]*6,h-25),str(cList[::-1][0]))
## words.fill = Color("black")
## words.rotate(90)
## words.draw(plot)

setmax = 35
num1 = 0
for x in range(len(bList)):
   if bList[x] <= setmax:
      num1 += 1
pct = num1/len(bList)
print("The percentage of differentials less than {} with {} trials is:".format(setmax,trials),pct)