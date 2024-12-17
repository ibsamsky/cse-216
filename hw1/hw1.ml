(* helper functions *)

let rec filter f = function
  | [] -> []
  | x :: xs -> if f x then x :: filter f xs else filter f xs
;;

let range n =
  let rec aux m n a = if n > m then a else aux m (n + 1) ((m - n) :: a) in
  aux n 0 []
;;

let rec repeat f n x = if n = 0 then x else repeat f (n - 1) (f x)

let rev l =
  let rec rev_acc l a =
    match l with
    | [] -> a
    | x :: xs -> rev_acc xs (x :: a)
  in
  rev_acc l []
;;

let partition f l =
  let rec aux same other = function
    | [] -> rev same, rev other
    | x :: xs -> if f x then aux (x :: same) other xs else aux same (x :: other) xs
  in
  aux [] [] l
;;

(* end helpers *)

(* Problem 1 *)
let rec compress = function
  | [] -> []
  | [ x ] -> [ x ]
  | xa :: xb :: xs -> if xa = xb then compress (xb :: xs) else xa :: compress (xb :: xs)
;;

(* assert (
   compress [ "a"; "a"; "b"; "c"; "c"; "a"; "a"; "d"; "e"; "e"; "e" ]
   = [ "a"; "b"; "c"; "a"; "d"; "e" ]) *)

(* Problem 2 *)
let rec remove_if l f =
  match l with
  | [] -> []
  | x :: xs -> if f x then remove_if xs f else x :: remove_if xs f
;;

(* assert (remove_if [ 1; 2; 3; 4; 5 ] (fun x -> x mod 2 = 1) = [ 2; 4 ]) *)

(* Problem 3 *)
let slice l i j =
  let rec take_n n = function
    | [] -> []
    | x :: xs -> if n <= 0 then [] else x :: take_n (n - 1) xs
  in
  let rec skip_n n = function
    | [] -> []
    | _ :: xs as l -> if n = 0 then l else skip_n (n - 1) xs
  in
  take_n (j - i) @@ skip_n i l
;;

(* assert (slice [ "a"; "b"; "c"; "d"; "e"; "f"; "g"; "h" ] 2 6 = [ "c"; "d"; "e"; "f" ]);; *)

(* assert (
   slice [ "a"; "b"; "c"; "d"; "e"; "f"; "g"; "h" ] 3 20 = [ "d"; "e"; "f"; "g"; "h" ])
   ;; *)

(* assert (slice [ "a"; "b"; "c"; "d"; "e"; "f"; "g"; "h" ] 3 2 = []) *)

(* Problem 4 *)
let equivs f l =
  let rec aux f l eq =
    match l with
    | [] -> rev eq
    | x :: _ ->
      let x_eq, rest = partition (f x) l in
      aux f rest (x_eq :: eq)
  in
  aux f l []
;;

(* assert (equivs ( = ) [ 1; 2; 3; 4 ] = [ [ 1 ]; [ 2 ]; [ 3 ]; [ 4 ] ]);; *)

(* assert (
   equivs (fun x y -> x mod 2 = y mod 2) [ 1; 2; 3; 4; 5; 6; 7; 8 ]
   = [ [ 1; 3; 5; 7 ]; [ 2; 4; 6; 8 ] ]) *)

(* Problem 5 *)
(* inefficient, but a fun way of doing it *)
let goldbachpair n =
  let is_prime n =
    let rec aux c = if n <= c then true else if n mod c = 0 then false else aux (c + 1) in
    if n < 2 then false else aux 2
  in
  let rec two_sum n = function
    | [] -> None, None
    | [ x ] -> Some x, None
    | h :: t ->
      (match List.find_opt (( = ) (n - h)) t with
       | Some v -> Some h, Some v
       | None -> two_sum n t)
  in
  if n mod 2 = 1 || n <= 2 then failwith "invalid input";
  match two_sum n @@ filter is_prime @@ range n with
  | Some x, Some y -> x, y
  | _, _ -> failwith "congratulations! :)"
;;

(* assert (goldbachpair 10 = (3, 7)) *)

(* Problem 6 *)
let rec identical_on f g = function
  | [] -> true
  | x :: xs -> if f x <> g x then false else identical_on f g xs
;;

(* assert (identical_on (fun i -> i * i) (fun i -> 3 * i) [ 3 ]);; *)
(* assert (not @@ identical_on (fun i -> i * i) (fun i -> 3 * i) [ 1; 2; 3 ]) *)

(* Problem 7 *)
(* according to hw/piazza (@56), type of `cmp` should be ('a * 'a) -> 'a *)
let rec pairwisefilter cmp = function
  | [] -> []
  | [ x ] -> [ x ]
  | xa :: xb :: xs ->
    let p = xa, xb in
    cmp p :: pairwisefilter cmp xs
;;

(* assert (
  let min p =
    let x, y = p in
    if x < y then x else y
  in
  pairwisefilter min [ 14; 11; 20; 25; 10; 11 ] = [ 11; 20; 10 ])
;; *)

(* assert (
  pairwisefilter
    (fun p ->
      let x, y = p in
      if String.length x < String.length y then x else y)
    [ "and"; "this"; "makes"; "shorter"; "strings"; "always"; "win" ]
  = [ "and"; "makes"; "always"; "win" ]) *)

(* Problem 8 *)
let polynomial tl =
  let exp n p = repeat (( * ) n) p 1 in
  let rec mono f tl =
    match tl with
    | [] -> f
    | th :: tt -> fun x -> (fst th * (exp x @@ snd th)) + (mono f tt @@ x)
  in
  mono (fun _ -> 0) tl
;;

(* assert (polynomial [ 3, 3; -2, 1; 5, 0 ] @@ 2 = 25) *)

(* Problem 9 *)
let rec suffixes = function
  | [] -> []
  | _ :: xs as l -> l :: suffixes xs
;;

(* assert (suffixes [ 1; 2; 5 ] = [ [ 1; 2; 5 ]; [ 2; 5 ]; [ 5 ] ]) *)

(* Problem 10 *)
let rec powerset s =
  let rec prepend a l =
    match l with
    | [] -> []
    | h :: t -> (a :: h) :: prepend a t
  in
  match s with
  | [] -> [ [] ]
  | h :: t -> prepend h (powerset t) @ powerset t
;;
