-- classic milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(1, 4, 1),  -- black tea bag
(1, 7, 1),  -- milk powder
(1, 21, 1), -- sugar
(1, 1, 1),  -- cup
(1, 2, 1),  -- straw
(1, 3, 1);  -- sealing film

-- taro milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(2, 8, 1),  -- taro powder
(2, 7, 1),  -- milk powder
(2, 21, 1), -- sugar
(2, 1, 1),  -- cup
(2, 2, 1),  -- straw
(2, 3, 1);  -- sealing film

-- thai milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(3, 4, 1),  -- black tea bag
(3, 7, 1),  -- milk powder
(3, 21, 1), -- sugar
(3, 1, 1),  -- cup
(3, 2, 1),  -- straw
(3, 3, 1);  -- sealing film

-- honeydew milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(4, 12, 1), -- fruit syrup
(4, 7, 1),  -- milk powder
(4, 21, 1), -- sugar
(4, 1, 1),  -- cup
(4, 2, 1),  -- straw
(4, 3, 1);  -- sealing film

-- matcha milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(5, 9, 1),  -- matcha powder
(5, 7, 1),  -- milk powder
(5, 21, 1), -- sugar
(5, 1, 1),  -- cup
(5, 2, 1),  -- straw
(5, 3, 1);  -- sealing film

-- brown sugar milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(6, 10, 1), -- brown sugar syrup
(6, 7, 1),  -- milk powder
(6, 1, 1),  -- cup
(6, 2, 1),  -- straw
(6, 3, 1);  -- sealing film

-- wintermelon milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(7, 11, 1), -- wintermelon syrup
(7, 7, 1),  -- milk powder
(7, 21, 1), -- sugar
(7, 1, 1),  -- cup
(7, 2, 1),  -- straw
(7, 3, 1);  -- sealing film

-- oolong milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(8, 6, 1),  -- oolong tea bag
(8, 7, 1),  -- milk powder
(8, 21, 1), -- sugar
(8, 1, 1),  -- cup
(8, 2, 1),  -- straw
(8, 3, 1);  -- sealing film

-- jasmine green milk tea
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(9, 5, 1),  -- green tea bag
(9, 7, 1),  -- milk powder
(9, 21, 1), -- sugar
(9, 1, 1),  -- cup
(9, 2, 1),  -- straw
(9, 3, 1);  -- sealing film

-- earl grey milk tea (menuid = 10)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(10, 4, 1),  -- black tea bag (used for earl grey)
(10, 7, 1),  -- milk powder
(10, 21, 1), -- sugar
(10, 1, 1),  -- cup
(10, 2, 1),  -- straw
(10, 3, 1);  -- sealing film

-- strawberry fruit tea (menuid = 11)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(11, 12, 1), -- fruit syrup (assumed strawberry-flavored)
(11, 15, 1), -- strawberry bits (ensure inventory has "strawberry bits")
(11, 1, 1),  -- cup
(11, 2, 1),  -- straw
(11, 3, 1);  -- sealing film

-- mango fruit tea (menuid = 12)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(12, 12, 1), -- fruit syrup (assumed mango-flavored)
(12, 14, 1), -- mango bits (ensure inventory has "mango bits")
(12, 1, 1),  -- cup
(12, 2, 1),  -- straw
(12, 3, 1);  -- sealing film

-- passion fruit green tea (menuid = 13)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(13, 12, 1), -- fruit syrup (assumed passion fruit-flavored)
(13, 5, 1),  -- green tea bag
(13, 1, 1),  -- cup
(13, 2, 1),  -- straw
(13, 3, 1);  -- sealing film

-- lychee black tea (menuid = 14)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(14, 4, 1),  -- black tea bag
(14, 13, 1), -- lychee jelly (ensure inventory has "lychee jelly")
(14, 7, 1),  -- milk powder (for a creamy version)
(14, 1, 1),  -- cup
(14, 2, 1),  -- straw
(14, 3, 1);  -- sealing film

-- peach oolong tea (menuid = 15)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(15, 6, 1),  -- oolong tea bag
(15, 16, 1), -- peach bits (ensure inventory has "peach bits")
(15, 1, 1),  -- cup
(15, 2, 1),  -- straw
(15, 3, 1);  -- sealing film

-- grapefruit green tea (menuid = 16)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(16, 5, 1),  -- green tea bag
(16, 12, 1), -- fruit syrup (assumed grapefruit-flavored using generic fruit syrup)
(16, 1, 1),  -- cup
(16, 2, 1),  -- straw
(16, 3, 1);  -- sealing film

-- lemon black tea (menuid = 17)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(17, 4, 1),  -- black tea bag
(17, 21, 1), -- sugar (or, if available, a lemon syrup ingredient)
(17, 1, 1),  -- cup
(17, 2, 1),  -- straw
(17, 3, 1);  -- sealing film

-- kiwi fruit tea (menuid = 18)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(18, 12, 1), -- fruit syrup (assumed kiwi-flavored using generic fruit syrup)
(18, 1, 1),  -- cup
(18, 2, 1),  -- straw
(18, 3, 1);  -- sealing film

-- coconut milk tea (menuid = 19)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(19, 12, 1),  -- fruit syrup
(19, 1, 1),  -- cup
(19, 2, 1),  -- straw
(19, 3, 1);  -- sealing film

-- almond milk tea (menuid = 20)
insert into drinkingredients (menuid, ingredientid, quantityused) values 
(20, 23, 1),  -- almond milk powder 
(20, 1, 1),  -- cup
(20, 2, 1),  -- straw
(20, 3, 1);  -- sealing film
