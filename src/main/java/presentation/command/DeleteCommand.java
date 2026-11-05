package presentation.command;

import presentation.console.Arguments;

public final class DeleteCommand extends LinkCommand {
  public DeleteCommand(LinkContext context) {
    super(context, "delete", "<ссылка>", "Удалить ссылку");
  }

  @Override
  protected void perform(Arguments args) {
    String code = code(args);

    links().delete(user(), code);
    terminal().success("Ссылка " + format().of(code) + " удалена");
  }
}
