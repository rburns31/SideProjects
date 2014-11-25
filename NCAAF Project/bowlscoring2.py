from random import randrange
def rep(m1,m2):
   p1 = 84
   p2 = 76
   p3 = 75
   p4 = 68
   scores = [0,0,0,0]
#   p1non = [] #Starting at championship game
#   p2non = []
#   p3non = []
#   p4non = []
   p1bcs = 1
   p2bcs = 0
   p3bcs = 0
   p4bcs = 0
#   k = randrange(2)
#   if k == p1non:
#      p1 += m1
#   if k == p2non:
#      p2 += m1
#   if k == p3non:
#      p3 += m1
#   if k == p4non:
#      p4 += m1
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
   bList = aList[:]
   bList.sort()
   loser,third,second,winner = bList
   for x in range(len(aList)):
      if aList[x] == loser:
         scores[x] = int(min(aList)-max(aList)) #lost
      if aList[x] == third:
         scores[x] = int(round(-1*(min(aList)-max(aList))/6)) #from last
         scores[x] += int(aList[x]-max(aList)) #lost
      if aList[x] == second:
         scores[x] = int(round(-1*(min(aList)-max(aList))/3)) #from last
         scores[x] += int(aList[x]-max(aList)) #lost
         scores[x] += int(round(-1*(bList[1]-max(aList))/3)) #from third
      if aList[x] == winner:
         scores[x] = int(-1*(min(aList)-max(aList)+round((-1*(min(aList)-max(aList))/6))+round((-1*(min(aList)-max(aList))/3)))) #from last
         scores[x] += int(-1*(bList[1]-max(aList))/1.5) #from third
         scores[x] += aList[x]-bList[2] #from second
   print(scores)
   return(scores)

trials = 100
print("After the Clemson game, and with {} trials:".format(trials))

aaronList = []
ryanList = []
kyleList = []
maxList = []
for x in range(trials):
   a,r,k,m = rep(4,7)
   aaronList.append(a)
   ryanList.append(r)
   kyleList.append(k)
   maxList.append(m)

print("Aaron's average payout is: {:.2f} dollars.".format(sum(aaronList)/len(aaronList)))
print("Ryan's average payout is: {:.2f} dollars.".format(sum(ryanList)/len(ryanList)))
print("Kyle's average payout is: {:.2f} dollars.".format(sum(kyleList)/len(kyleList)))
print("Max's average payout is: {:.2f} dollars.".format(sum(maxList)/len(maxList)))