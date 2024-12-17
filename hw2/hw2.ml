(* HELPERS *)

let ( @. ) = Fun.compose

let print_list f =
  let stringify l = "[" ^ String.concat "; " l ^ "]" in
  print_endline @. stringify @. List.map f
;;

let explode s = s |> String.to_seq |> List.of_seq
let implode c = c |> List.to_seq |> String.of_seq
let count f = List.length @. List.filter f
let any f = List.fold_left ( || ) false @. List.map f
let contains_any itm scr = any (Fun.flip ( @@ ) scr) @@ List.map List.mem itm

let take_while f l =
  let rec aux l acc =
    match l with
    | h :: t when f h -> aux t (h :: acc)
    | _ -> List.rev acc, l
  in
  aux l []
;;

(* --------------------- Problem 1 --------------------- *)

type 'a binarytree =
  | Leaf
  | Node of 'a * 'a binarytree * 'a binarytree

let rec walk_inorder = function
  | Leaf -> []
  | Node (v, l, r) -> walk_inorder l @ (v :: walk_inorder r)
;;

(* TEST CASES *)

let test_tree =
  Node
    ( 1
    , Node (2, Node (4, Leaf, Leaf), Node (5, Leaf, Leaf))
    , Node (3, Node (9, Leaf, Leaf), Node (0, Leaf, Leaf)) )
;;

print_string "walk_inorder test_tree = ";
print_list string_of_int @@ walk_inorder test_tree

(* --------------------- Problem 2 --------------------- *)

type ('a, 'b) tree =
  | Leaf of 'a
  | Tree of ('a, 'b) node

and ('a, 'b) node =
  { operator : 'b
  ; left : ('a, 'b) tree
  ; right : ('a, 'b) tree
  }

type item_type =
  | Operators
  | Operands

(* allow lowercase *)
let operators = Operators
let operands = Operands

type ('a, 'b) operator_or_operand =
  | Operator of 'b
  | Operand of 'a

let rec items_of_parameter_type tree ty =
  match ty with
  | Operators ->
    (match tree with
     | Leaf _ -> []
     | Tree n ->
       items_of_parameter_type n.left ty
       @ (Operator n.operator :: items_of_parameter_type n.right ty))
  | Operands ->
    (match tree with
     | Leaf v -> [ Operand v ]
     | Tree n -> items_of_parameter_type n.left ty @ items_of_parameter_type n.right ty)
;;

(* --------------------- Problem 3 --------------------- *)

exception ParseError of string
exception TreeBuildError of string

let is_numeric char =
  let cc = Char.code char in
  cc >= Char.code '0' && cc <= Char.code '9'
;;

let is_num_or_decimal char = is_numeric char || char = '.'

(* TOKENS *)

type int_or_float =
  | Int of int
  | Float of float

let unwrap_int = function
  | Int v -> v
  | _ -> raise (Invalid_argument "not Int")
;;

let unwrap_float = function
  | Float v -> v
  | _ -> raise (Invalid_argument "not Float")
;;

type token =
  | LPar
  | RPar
  | Op of string
  | Num of int_or_float

let to_float ts =
  let match_tok = function
    | Num (Int v) -> Num (Float (float_of_int v))
    | t -> t
  in
  List.map match_tok ts
;;

let balanced tokens =
  let rec aux stack = function
    | [] -> stack = []
    | LPar :: tt -> aux (LPar :: stack) tt
    | RPar :: tt -> if stack = [] then false else aux (List.tl stack) tt
    | _ :: tt -> aux stack tt
  in
  aux [] tokens
;;

let tokenize expr_str =
  let rec consume_chars chars tokens =
    match chars with
    | [] -> tokens (* reversed *)
    | ch :: ct ->
      (match ch with
       | '(' -> consume_chars ct (LPar :: tokens)
       | ')' -> consume_chars ct (RPar :: tokens)
       | _ ->
         let num, rem = take_while is_num_or_decimal chars in
         let num_str = implode num in
         if String.contains num_str '.'
         then consume_chars rem (Num (Float (float_of_string num_str)) :: tokens)
         else consume_chars rem (Num (Int (int_of_string num_str)) :: tokens))
  in
  let rec consume_strs strs tokens =
    match strs with
    | [] -> List.rev tokens
    | sh :: st when any (( = ) sh) [ "+"; "-"; "*"; "/"; "+."; "-."; "*."; "/." ] ->
      consume_strs st (Op sh :: tokens)
    | sh :: st -> consume_strs st (consume_chars (explode sh) tokens)
  in
  let split_str = String.split_on_char ' ' expr_str in
  consume_strs split_str []
;;

let rec parse_expr tok =
  let l, rem = parse_term tok in
  parse_op l rem

and parse_term tok =
  match tok with
  | LPar :: tt ->
    let expr, rem = parse_expr tt in
    (match rem with
     | RPar :: rest -> expr, rest
     | _ -> raise (ParseError "expected closing parenthesis"))
  | Num v :: tt -> Leaf v, tt
  | _ -> raise (ParseError "expr is not well-formed")

and parse_op lhs tok =
  match tok with
  | Op o :: tt ->
    let rhs, rem = parse_expr tt in
    parse_op (Tree { operator = o; left = lhs; right = rhs }) rem
  | _ -> lhs, tok
;;

(* END TOKENS *)

let build_tree expr =
  let tokens = tokenize expr in
  if not @@ balanced tokens
  then raise (TreeBuildError "expression has unbalanced parentheses");
  let nums_ct =
    count
      (fun t ->
        match t with
        | Num _ -> true
        | _ -> false)
      tokens
  in
  let pars_ct = count (fun t -> t = LPar || t = RPar) tokens in
  (* i = l - 1; 2 parens for each internal *)
  if 2 * (nums_ct - 1) > pars_ct
  then raise (TreeBuildError "expression is not fully parenthesized");
  let tokens =
    if contains_any [ Op "+."; Op "-."; Op "*."; Op "/." ] tokens
    then to_float tokens
    else tokens
  in
  if any
       (fun t ->
         match t with
         | Num (Float _) -> true
         | _ -> false)
       tokens
     && contains_any [ Op "+"; Op "-"; Op "*"; Op "/" ] tokens
  then raise (TreeBuildError "incompatible operator types");
  let parsed, rem = parse_expr tokens in
  if rem <> [] then raise (TreeBuildError "unparsed tokens");
  parsed
;;

(* --------------------- Problem 4 --------------------- *)

let rec evaluate tree =
  match tree with
  | Leaf v -> v
  | Tree { operator; left; right } ->
    let left, right = evaluate left, evaluate right in
    (match operator with
     | "+" -> Int (unwrap_int left + unwrap_int right)
     | "-" -> Int (unwrap_int left - unwrap_int right)
     | "*" -> Int (unwrap_int left * unwrap_int right)
     | "/" -> Int (unwrap_int left / unwrap_int right)
     | "+." -> Float (unwrap_float left +. unwrap_float right)
     | "-." -> Float (unwrap_float left -. unwrap_float right)
     | "*." -> Float (unwrap_float left *. unwrap_float right)
     | "/." -> Float (unwrap_float left /. unwrap_float right)
     | _ -> failwith "invalid operator")
;;

(* TEST CASES *)

let int_t = build_tree "(1 + (2 * 3))"
let int_v = evaluate int_t;;

assert (unwrap_int int_v = 7)

let float_t = build_tree "((1.5 +. 2) *. 3)"
let float_v = evaluate float_t;;

assert (unwrap_float float_v = 10.5)

(* failing case *)
let fail =
  try build_tree "1 + 2" with
  | TreeBuildError _ -> Leaf (Float nan)
;;

assert (Float.is_nan @@ unwrap_float @@ evaluate fail)

(* --------------------- Problem 5 --------------------- *)

type expr =
  | Const of int
  | Var of string
  | Add of expr * expr
  | Mul of expr * expr

let reduce expr =
  let rec rearrange = function
    | (Const _ | Var _) as e -> e
    (* bring var to right *)
    | Mul ((Var _ as v), ((Add (_, _) | Mul (_, _)) as o)) -> Mul (rearrange o, v)
    | Add ((Var _ as v), ((Add (_, _) | Mul (_, _)) as o)) -> Add (rearrange o, v)
    (* bring const to left *)
    | Mul (o, (Const _ as c)) -> Mul (c, rearrange o)
    | Add (o, (Const _ as c)) -> Add (c, rearrange o)
    (* catch-all *)
    | Add (e1, e2) -> Add (rearrange e1, rearrange e2)
    | Mul (e1, e2) -> Mul (rearrange e1, rearrange e2)
  in
  let rec reduce_rest = function
    | (Const _ | Var _) as e -> e
    (* identities *)
    | Add (Const 0, e) | Add (e, Const 0) -> reduce_rest e
    | Mul (Const 1, e) | Mul (e, Const 1) -> reduce_rest e
    | Mul (Const 0, _) | Mul (_, Const 0) -> Const 0
    | Add (Const a, Const b) -> Const (a + b)
    | Mul (Const a, Const b) -> Const (a * b)
    (* binomial expansion *)
    | Mul (Add (a, b), Add (c, d)) ->
      let f = Mul (a, c) in
      let o = Mul (a, d) in
      let i = Mul (b, c) in
      let l = Mul (b, d) in
      Add (Add (f, o), Add (i, l))
    (* distribute over addition *)
    | Mul (m, Add (o1, o2)) -> reduce_rest (Add (Mul (m, o1), Mul (m, o2)))
    (* associativity of multiplication *)
    | Mul (Const c1, Mul (Const c2, o)) -> reduce_rest (Mul (Const (c1 * c2), o))
    | Mul (Mul (Const c, o1), o2) | Mul (o1, Mul (Const c, o2)) ->
      reduce_rest (Mul (Const c, Mul (o1, o2)))
    (* associativity of addition *)
    | Add (Const c1, Add (Const c2, o)) -> reduce_rest (Add (Const (c1 + c2), o))
    | Add (Add (Const c, o1), o2) | Add (o1, Add (Const c, o2)) ->
      reduce_rest (Add (Const c, Add (o1, o2)))
    (* variable-like special cases *)
    | Add (o1, o2) when o1 = o2 -> reduce_rest (Mul (Const 2, o1))
    | Add (o1, Mul (Const c, o2)) when o1 = o2 -> reduce_rest (Mul (Const (c + 1), o1))
    | Add (Mul (Const c, o1), o2) when o1 = o2 -> reduce_rest (Mul (Const (c + 1), o1))
    | Add (Mul (Const c1, o1), Mul (Const c2, o2)) when o1 = o2 ->
      reduce_rest (Mul (Const (c1 + c2), o1))
    (* catch-all *)
    | Add (e1, e2) -> Add (reduce_rest e1, reduce_rest e2)
    | Mul (e1, e2) -> Mul (reduce_rest e1, reduce_rest e2)
  in
  let reduce' = reduce_rest @. rearrange in
  (* i got lazy 🥲 *)
  let res = ref (reduce' expr) in
  while !res <> reduce' !res do
    res := reduce' !res
  done;
  !res
;;

(* TEST CASES *)

assert (reduce (Add (Const 0, Var "x")) = Var "x");;
assert (reduce (Mul (Const 1, Var "y")) = Var "y");;
assert (reduce (Mul (Const 1, Add (Const 2, Const 4))) = Const 6)

(* should be equal to 7x^2 + 112x + 336 *)
let binom =
  reduce (Mul (Add (Var "x", Const 4), Add (Add (Const 0, Var "x"), Mul (Add (Add (Add (Const 4, Const 8), Const 2), Var "x"), Add (Const 1, Const 5)))))
;;

assert (
  match binom with
  | Add (Const 336, _) -> true
  | _ -> false)

(* --------------------- Problem 6 --------------------- *)

let safesqrt fl =
  let r = sqrt fl in
  if Float.is_nan r then None else Some r
;;

let process_all l f_opt =
  List.map Option.get @@ List.filter Option.is_some @@ List.map f_opt l
;;

assert (process_all [ -1.0; 0.0; 4.0; 9.0; -16.0; 25.0 ] safesqrt = [ 0.; 2.; 3.; 5. ])
