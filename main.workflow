workflow "Clean" {
  on = "push"
  resolves = ["gradle"]
}

action "gradle" {
  uses = "gradle"
  runs = "clean"
}
